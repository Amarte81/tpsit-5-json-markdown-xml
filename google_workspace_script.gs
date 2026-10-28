/**
 * ============================================================================
 * GOOGLE WORKSPACE SCRIPT — LABORATORIO DIGITALE TPSIT CLASSE 5ª
 * Monitoraggio Mini-Test lungo lo scorrimento e Sfida dei 3 Casi Reali
 * ============================================================================
 * ISTRUZIONI PER IL DOCENTE (2 MINUTI):
 * 1. Crea un Foglio Google sul tuo account istituzionale (@scuola.edu.it)
 * 2. Clicca su Estensioni > Apps Script
 * 3. Incolla questo codice e salva il progetto
 * 4. Esegui la funzione `setupDashboardClasse()` per predisporre le tabelle e il calcolo automatico del KPI >= 90%
 * 5. Clicca su "Implementa" > "Nuova implementazione" > Tipo: "Applicazione web"
 *    - Esegui come: "Me stesso"
 *    - Chi ha accesso: "Chiunque all'interno del dominio scuola" (o "Chiunque" per test rapidi)
 * 6. Copia l'URL dell'applicazione web e incollalo nel campo "URL Web App Apps Script Docente" in fondo alla pagina web!
 */

const SHEET_RISULTATI = 'Risultati_Studenti';
const SHEET_KPI = 'KPI_Didattico_90';

/**
 * 1. INIZIALIZZA IL FOGLIO GOOGLE CON TABELLE E CRUSCOTTO CRITERIO >= 90%
 */
function setupDashboardClasse() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();

  // Foglio Risultati_Studenti
  let sheetRis = ss.getSheetByName(SHEET_RISULTATI);
  if (!sheetRis) {
    sheetRis = ss.insertSheet(SHEET_RISULTATI);
  }
  sheetRis.clear();

  const headers = [
    'Timestamp',
    'Email Studente (@scuola.edu.it)',
    'Componenti Coppia',
    'Mini-Test Superati (su 9)',
    'Caso 1: IoT & Java (JSON)',
    'Caso 2: GitHub & AI (Markdown)',
    'Caso 3: Webhook Ibrido & Scelta XML',
    'Casi Reali Superati (su 3)',
    'Avanzamento Totale (%)',
    'Tempo Impiegato (sec)',
    'Stato Obiettivo (3/3 Casi)'
  ];

  sheetRis.getRange(1, 1, 1, headers.length).setValues([headers]);
  sheetRis.getRange(1, 1, 1, headers.length)
    .setBackground('#0f172a')
    .setFontColor('#38bdf8')
    .setFontWeight('bold');
  sheetRis.setFrozenRows(1);
  sheetRis.autoResizeColumns(1, headers.length);

  // Foglio KPI_Didattico_90
  let sheetKpi = ss.getSheetByName(SHEET_KPI);
  if (!sheetKpi) {
    sheetKpi = ss.insertSheet(SHEET_KPI);
  }
  sheetKpi.clear();

  const kpiData = [
    ['CRUSCOTTO MONITORAGGIO DIDATTICO TPSIT — CLASSE 5ª', ''],
    ['Metrica', 'Valore in Tempo Reale'],
    ['Studenti / Coppie Partecipanti', '=COUNTA(Risultati_Studenti!B2:B250)'],
    ['Coppie con Successo Completo (3/3 Casi Reali)', '=COUNTIF(Risultati_Studenti!K2:K250; "SUPERATO")'],
    ['Percentuale di Successo Classe', '=IF(B3=0; 0; B4/B3)'],
    ['Target Ministeriale / Brief Didattico', 0.90],
    ['STATO CRITERIO DI SUCCESSO (Target >= 90%)', '=IF(B3=0; "IN ATTESA DI CONSEGNE"; IF(B5>=0.9; "🎯 OBIETTIVO RAGGIUNTO (>= 90%)"; "⏳ IN CORSO (< 90%)"))']
  ];

  sheetKpi.getRange(1, 1, kpiData.length, 2).setValues(kpiData);
  sheetKpi.getRange('A1:B1').merge().setBackground('#0b1120').setFontColor('#38bdf8').setFontWeight('bold').setFontSize(14);
  sheetKpi.getRange('A2:B2').setBackground('#1e293b').setFontColor('#ffffff').setFontWeight('bold');
  sheetKpi.getRange('B5:B6').setNumberFormat('0.0%');
  sheetKpi.getRange('A7:B7').setBackground('#ecfdf5').setFontColor('#065f46').setFontWeight('bold').setFontSize(13);
  sheetKpi.setColumnWidth(1, 380);
  sheetKpi.setColumnWidth(2, 280);
}

/**
 * 2. ENDPOINT RICEZIONE DATI DALLA WEB APP STUDENTI
 */
function doPost(e) {
  try {
    const ss = SpreadsheetApp.getActiveSpreadsheet();
    let sheet = ss.getSheetByName(SHEET_RISULTATI);
    if (!sheet) {
      setupDashboardClasse();
      sheet = ss.getSheetByName(SHEET_RISULTATI);
    }

    const payload = JSON.parse(e.postData.contents);
    const email = (payload.email || 'non_indicata@scuola.edu.it').trim();
    const studentNames = (payload.studentNames || 'Coppia Anonima').trim();
    const mtPassed = Number(payload.miniTestsPassed || 0);

    const c1Status = payload.case1Passed ? 'OK (JSON riparato + Scelta)' : 'Non completato';
    const c2Status = payload.case2Passed ? 'OK (MD riparato + Scelta)' : 'Non completato';
    const c3Status = payload.case3Passed ? 'OK (Webhook Ibrido + XML)' : 'Non completato';

    const casesCount = Number(payload.casesCompletedCount || (payload.case1Passed ? 1 : 0) + (payload.case2Passed ? 1 : 0) + (payload.case3Passed ? 1 : 0));
    const totalScorePct = Math.round(((mtPassed + casesCount) / 12) * 100);
    const elapsedSec = Number(payload.elapsedSeconds || 0);
    const verdict = casesCount >= 3 ? 'SUPERATO' : 'PARZIALE';

    sheet.appendRow([
      new Date(),
      email,
      studentNames,
      mtPassed + ' / 9',
      c1Status,
      c2Status,
      c3Status,
      casesCount + ' / 3',
      totalScorePct + '%',
      elapsedSec,
      verdict
    ]);

    return ContentService
      .createTextOutput(JSON.stringify({
        status: 'success',
        verdict: verdict,
        casesCount: casesCount,
        message: 'Consegna registrata correttamente nel foglio Google di classe!'
      }))
      .setMimeType(ContentService.MimeType.JSON);

  } catch (err) {
    return ContentService
      .createTextOutput(JSON.stringify({
        status: 'error',
        message: err.toString()
      }))
      .setMimeType(ContentService.MimeType.JSON);
  }
}
