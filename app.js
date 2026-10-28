/**
 * ============================================================================
 * LOGICA INTERATTIVA WEB APP — GUIDA DIGITALE TPSIT CLASSE 5ª
 * ============================================================================
 */

// Il docente può incollare qui l'URL della propria Web App Google Apps Script
// oppure passarlo direttamente nel link agli studenti: ?webhook=https://script.google.com/...
const DEFAULT_TEACHER_WEBHOOK_URL = '';

document.addEventListener('DOMContentLoaded', () => {
  const sessionStartTime = Date.now();

  // Pre-compila l'URL dello script Google Workspace se presente nel parametro ?webhook= o nella costante
  const urlParams = new URLSearchParams(window.location.search);
  const webhookFromQuery = urlParams.get('webhook') || DEFAULT_TEACHER_WEBHOOK_URL;
  const webhookInputEl = document.getElementById('finalTeacherWebhookUrl');
  if (webhookInputEl && webhookFromQuery) {
    webhookInputEl.value = webhookFromQuery;
  }

  // --------------------------------------------------------------------------
  // 1. STATO GLOBALE DEI PROGRESSI (6 MINI-TEST + 3 CASI REALI = 9 VERIFICHE)
  // --------------------------------------------------------------------------
  const learningState = {
    miniTests: {
      mt1: false,
      mt2: false,
      mt3: false,
      mt4: false,
      mt5: false,
      mt6: false
    },
    cases: {
      c1: false,
      c2: false,
      c3: false
    }
  };

  function updateGlobalProgress() {
    const mtPassed = Object.values(learningState.miniTests).filter(Boolean).length;
    const casesPassed = Object.values(learningState.cases).filter(Boolean).length;
    const totalPassed = mtPassed + casesPassed;
    const percentage = Math.round((totalPassed / 9) * 100);

    const progressBar = document.getElementById('globalProgressBar');
    const summaryLabel = document.getElementById('progressSummaryLabel');
    const mtBadge = document.getElementById('miniTestsScoreBadge');
    const casesBadge = document.getElementById('casesScoreBadge');
    const finalBadge = document.getElementById('finalCasesStatusBadge');

    if (progressBar) progressBar.style.width = `${percentage}%`;
    if (summaryLabel) summaryLabel.innerHTML = `Avanzamento Totale: <strong>${percentage}%</strong>`;
    if (mtBadge) mtBadge.textContent = `Mini-Test: ${mtPassed} / 6 Superati`;
    if (casesBadge) casesBadge.textContent = `Casi Reali: ${casesPassed} / 3 Superati`;
    if (finalBadge) {
      finalBadge.textContent = `Progresso Casi: ${casesPassed} / 3 Superati`;
      finalBadge.style.background = casesPassed === 3 ? 'var(--accent-green)' : 'var(--accent-amber)';
      finalBadge.style.color = casesPassed === 3 ? '#ffffff' : '#070b14';
    }
  }

  // --------------------------------------------------------------------------
  // 2. STRUMENTI DI ACCESSIBILITÀ & STAMPA
  // --------------------------------------------------------------------------
  const btnToggleLegibility = document.getElementById('btnToggleLegibility');
  if (btnToggleLegibility) {
    btnToggleLegibility.addEventListener('click', () => {
      document.body.classList.toggle('high-legibility');
      const isHigh = document.body.classList.contains('high-legibility');
      btnToggleLegibility.textContent = isHigh
        ? '🌙 Modalità Scura Standard'
        : '👁️ Alta Leggibilità (DSA/PDP)';
    });
  }

  const btnPrintWorksheet = document.getElementById('btnPrintWorksheet');
  if (btnPrintWorksheet) {
    btnPrintWorksheet.addEventListener('click', () => {
      window.print();
    });
  }

  // --------------------------------------------------------------------------
  // 3. PARTE 1 — AGGANCI VISIVI & SIMULAZIONE NOTIFICA PUSH
  // --------------------------------------------------------------------------
  const bubble = document.getElementById('hook1VisualBubble');
  const hookCode = document.getElementById('hook1CodeDisplay');
  const hookAlert = document.getElementById('hook1StatusAlert');

  const normalPayloadText = `{\n  "chatId": "5A_INF_2026",\n  "mittente": "Prof. TPSIT",\n  "timestamp": 1761642300,\n  "letto": false,\n  "testo": "Il server REST è attivo sulla porta 8080!"\n}`;
  const brokenPayloadText = `{\n  chatId: "5A_INF_2026",\n  "mittente": "Prof. TPSIT",\n  "timestamp": 1761642300,\n  "letto": FALSE,\n  "testo": "Il server REST è attivo sulla porta 8080!",\n}`;

  const btnHook1Normal = document.getElementById('btnHook1Normal');
  const btnHook1Broken = document.getElementById('btnHook1Broken');

  if (btnHook1Normal && btnHook1Broken && bubble && hookCode && hookAlert) {
    btnHook1Normal.addEventListener('click', () => {
      bubble.className = 'msg-bubble';
      bubble.innerHTML = `
        <div style="font-size: 0.8rem; color: var(--accent-blue); font-weight: 800;">Gruppo 5ª INF • Adesso</div>
        <p style="margin-top: 0.3rem;"><strong>Prof. TPSIT:</strong> Il server REST è attivo sulla porta 8080!</p>
      `;
      hookCode.textContent = normalPayloadText;
      hookAlert.className = 'status-banner status-ok';
      hookAlert.textContent = '✅ Il payload rispetta rigorosamente le specifiche RFC 8259: chiavi racchiuse tra doppi apici e booleano "false" minuscolo.';
    });

    btnHook1Broken.addEventListener('click', () => {
      bubble.className = 'msg-bubble alert-err';
      bubble.innerHTML = `
        <div style="font-weight: 800; color: #fca5a5;">⚠️ CRASH NOTIFICA SMARTPHONE</div>
        <div style="margin-top: 0.3rem;">SyntaxError: Unexpected token } in JSON at line 7. Impossibile mostrare il messaggio.</div>
      `;
      hookCode.textContent = brokenPayloadText;
      hookAlert.className = 'status-banner status-error';
      hookAlert.textContent = '❌ ANOMALIE RILEVATE: 1) Chiave "chatId" senza doppi apici; 2) Booleano "FALSE" maiuscolo; 3) Virgola finale dopo l\'ultimo elemento!';
    });
  }

  // --------------------------------------------------------------------------
  // 4. MINI-TEST 1.1 & 1.2 (PARTE 1)
  // --------------------------------------------------------------------------
  const btnCheckMt1 = document.getElementById('btnCheckMiniTest1');
  const feedbackMt1 = document.getElementById('feedbackMiniTest1');
  if (btnCheckMt1 && feedbackMt1) {
    btnCheckMt1.addEventListener('click', () => {
      const selected = document.querySelector('input[name="mt1"]:checked');
      feedbackMt1.style.display = 'block';
      if (!selected) {
        feedbackMt1.className = 'mini-test-feedback status-warn';
        feedbackMt1.textContent = '⚠️ Seleziona un\'opzione prima di verificare.';
        return;
      }
      if (selected.value === 'optC') {
        learningState.miniTests.mt1 = true;
        feedbackMt1.className = 'mini-test-feedback status-ok';
        feedbackMt1.innerHTML = '🎉 <strong>Corretto!</strong> In JSON le chiavi <em>devono</em> avere i doppi apici (<code>"mittente"</code>), i booleani sono solo minuscoli (<code>true/false</code>) e la virgola finale prima di <code>}</code> è severamente vietata.';
      } else {
        feedbackMt1.className = 'mini-test-feedback status-error';
        feedbackMt1.textContent = '❌ Risposta errata. Rileggi il payload: osserva la prima chiave senza virgolette e la virgola prima della graffa di chiusura.';
      }
      updateGlobalProgress();
    });
  }

  const btnCheckMt2 = document.getElementById('btnCheckMiniTest2');
  const feedbackMt2 = document.getElementById('feedbackMiniTest2');
  if (btnCheckMt2 && feedbackMt2) {
    btnCheckMt2.addEventListener('click', () => {
      const selected = document.querySelector('input[name="mt2"]:checked');
      feedbackMt2.style.display = 'block';
      if (!selected) {
        feedbackMt2.className = 'mini-test-feedback status-warn';
        feedbackMt2.textContent = '⚠️ Seleziona un\'opzione prima di verificare.';
        return;
      }
      if (selected.value === 'optB') {
        learningState.miniTests.mt2 = true;
        feedbackMt2.className = 'mini-test-feedback status-ok';
        feedbackMt2.innerHTML = '🎉 <strong>Esatto!</strong> Java è un linguaggio a tipizzazione statica e forte. Quando la libreria (es. <code>Jackson</code> o <code>Gson</code>) cerca di assegnare la stringa <code>"42_livello"</code> all\'attributo <code>int level</code>, lancia un\'eccezione e l\'app va in crash.';
      } else {
        feedbackMt2.className = 'mini-test-feedback status-error';
        feedbackMt2.textContent = '❌ Risposta errata. Ricorda che Java è fortemente tipizzato e non può convertire automaticamente testo alfanumerico in una variabile intera int.';
      }
      updateGlobalProgress();
    });
  }

  // --------------------------------------------------------------------------
  // 5. BANCO DI PROVA PARTE 1 (VALIDATORE JSON -> GENERATORE CLASSE JAVA)
  // --------------------------------------------------------------------------
  const p1JsonInput = document.getElementById('p1SandboxJson');
  const p1JsonStatus = document.getElementById('p1SandboxStatus');
  const p1JavaOutput = document.getElementById('p1SandboxJavaCode');

  function inferJavaType(val) {
    if (val === null) return 'Object';
    if (Array.isArray(val)) {
      if (val.length === 0) return 'List<Object>';
      const inner = inferJavaType(val[0]);
      return `${inner}[]`;
    }
    if (typeof val === 'boolean') return 'boolean';
    if (typeof val === 'number') return Number.isInteger(val) ? 'int' : 'double';
    if (typeof val === 'string') return 'String';
    if (typeof val === 'object') return 'Map<String, Object>';
    return 'Object';
  }

  function generateJavaClass(obj) {
    if (typeof obj !== 'object' || obj === null || Array.isArray(obj)) {
      return '// Inserisci un oggetto JSON { ... } alla radice per generare la classe Java.';
    }
    const lines = ['public class DatiRicevuti {'];
    Object.entries(obj).forEach(([key, val]) => {
      const fieldName = key.replace(/[^a-zA-Z0-9_]/g, '_');
      const jType = inferJavaType(val);
      lines.push(`    private ${jType} ${fieldName};`);
    });
    lines.push('');
    lines.push('    // Costruttore predefinito richiesto dai parser (Gson/Jackson)');
    lines.push('    public DatiRicevuti() {}');
    lines.push('}');
    return lines.join('\n');
  }

  function updateP1Sandbox() {
    if (!p1JsonInput || !p1JsonStatus || !p1JavaOutput) return;
    const raw = p1JsonInput.value;
    try {
      const parsed = JSON.parse(raw);
      p1JsonStatus.className = 'status-banner status-ok';
      p1JsonStatus.textContent = '✅ JSON Ben Formato! Pronto per la deserializzazione nel server Java.';
      p1JavaOutput.textContent = generateJavaClass(parsed);
    } catch (err) {
      p1JsonStatus.className = 'status-banner status-error';
      p1JsonStatus.textContent = `❌ Errore Sintattico: ${err.message}. Controlla virgolette doppie, virgole e booleani!`;
      p1JavaOutput.textContent = '// Correggi il JSON sopra per rigenerare la classe Java.';
    }
  }

  if (p1JsonInput) {
    p1JsonInput.addEventListener('input', updateP1Sandbox);
    updateP1Sandbox();
  }

  // --------------------------------------------------------------------------
  // 6. MINI-TEST 2.1 & 2.2 (PARTE 2)
  // --------------------------------------------------------------------------
  const btnCheckMt3 = document.getElementById('btnCheckMiniTest3');
  const feedbackMt3 = document.getElementById('feedbackMiniTest3');
  if (btnCheckMt3 && feedbackMt3) {
    btnCheckMt3.addEventListener('click', () => {
      const selected = document.querySelector('input[name="mt3"]:checked');
      feedbackMt3.style.display = 'block';
      if (!selected) {
        feedbackMt3.className = 'mini-test-feedback status-warn';
        feedbackMt3.textContent = '⚠️ Seleziona un\'opzione prima di verificare.';
        return;
      }
      if (selected.value === 'optA') {
        learningState.miniTests.mt3 = true;
        feedbackMt3.className = 'mini-test-feedback status-ok';
        feedbackMt3.innerHTML = '🎉 <strong>Corretto!</strong> Nello standard CommonMark (usato da GitHub e dai modelli IA), lo spazio dopo <code>#</code> è tassativo (es. <code># Titolo</code>). Senza spazio viene letto come una stringa qualsiasi o un tag.';
      } else {
        feedbackMt3.className = 'mini-test-feedback status-error';
        feedbackMt3.textContent = '❌ Risposta errata. Ricorda la regola dello spazio obbligatorio dopo il cancelletto.';
      }
      updateGlobalProgress();
    });
  }

  const btnCheckMt4 = document.getElementById('btnCheckMiniTest4');
  const feedbackMt4 = document.getElementById('feedbackMiniTest4');
  if (btnCheckMt4 && feedbackMt4) {
    btnCheckMt4.addEventListener('click', () => {
      const selected = document.querySelector('input[name="mt4"]:checked');
      feedbackMt4.style.display = 'block';
      if (!selected) {
        feedbackMt4.className = 'mini-test-feedback status-warn';
        feedbackMt4.textContent = '⚠️ Seleziona un\'opzione prima di verificare.';
        return;
      }
      if (selected.value === 'optA') {
        learningState.miniTests.mt4 = true;
        feedbackMt4.className = 'mini-test-feedback status-ok';
        feedbackMt4.innerHTML = '🎉 <strong>Perfetto!</strong> La sintassi Markdown per i collegamenti ipertestuali prevede prima le parentesi quadre per il testo visibile e poi le tonde per l\'indirizzo: <code>[Testo del Link](URL)</code>.';
      } else {
        feedbackMt4.className = 'mini-test-feedback status-error';
        feedbackMt4.textContent = '❌ Risposta errata. L\'errore comune è invertire le parentesi: la formula corretta è [Testo](URL).';
      }
      updateGlobalProgress();
    });
  }

  // --------------------------------------------------------------------------
  // 7. BANCO DI PROVA PARTE 2 (EDITOR MARKDOWN IN TEMPO REALE)
  // --------------------------------------------------------------------------
  const p2MdInput = document.getElementById('p2SandboxMdInput');
  const p2MdPreview = document.getElementById('p2SandboxMdPreview');

  function parseMarkdownToHtml(md) {
    let out = md
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;');

    // Blocchi codice ```lang ... ```
    out = out.replace(/```([a-zA-Z0-9_-]*)\n([\s\S]*?)```/g, (_, lang, code) => {
      return `<pre><code>${code.trim()}</code></pre>`;
    });

    // Tabelle semplici
    out = out.replace(
      /^\|(.+)\|\s*\n\|(\s*[-:]+\s*\|)+\s*\n((\|.*\|\s*\n?)+)/gm,
      (match, headerRow, _, bodyRows) => {
        const headers = headerRow.split('|').map((s) => s.trim()).filter(Boolean);
        const rows = bodyRows.trim().split('\n').map((r) =>
          r.split('|').map((c) => c.trim()).filter(Boolean)
        );
        const thHtml = headers.map((h) => `<th>${h}</th>`).join('');
        const trHtml = rows
          .map((r) => `<tr>${r.map((c) => `<td>${c}</td>`).join('')}</tr>`)
          .join('');
        return `<table class="data-table" style="margin:0.6rem 0;"><thead><tr>${thHtml}</tr></thead><tbody>${trHtml}</tbody></table>`;
      }
    );

    // Titoli
    out = out.replace(/^###\s+(.+)$/gm, '<h3 style="color:var(--accent-blue); margin:0.4rem 0;">$1</h3>');
    out = out.replace(/^##\s+(.+)$/gm, '<h2 style="color:var(--accent-blue); margin:0.5rem 0;">$1</h2>');
    out = out.replace(/^#\s+(.+)$/gm, '<h1 style="color:var(--accent-blue); margin:0.6rem 0;">$1</h1>');

    // Inline format
    out = out.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>');
    out = out.replace(/\*(.+?)\*/g, '<em>$1</em>');
    out = out.replace(/`([^`\n]+)`/g, '<code>$1</code>');
    out = out.replace(/\[([^\]]+)\]\((https?:\/\/[^\s)]+)\)/g, '<a href="$2" target="_blank" style="color:var(--accent-blue); font-weight:700;">$1</a>');

    // Liste puntate
    out = out.replace(/^- (.+)$/gm, '<li>$1</li>');
    out = out.replace(/(<li>.*<\/li>)/gs, '<ul class="accessible-list">$1</ul>');

    // Newlines
    out = out.replace(/\n\n/g, '<br><br>');
    return out;
  }

  if (p2MdInput && p2MdPreview) {
    p2MdInput.addEventListener('input', () => {
      p2MdPreview.innerHTML = parseMarkdownToHtml(p2MdInput.value);
    });
    p2MdPreview.innerHTML = parseMarkdownToHtml(p2MdInput.value);
  }

  // --------------------------------------------------------------------------
  // 8. PARTE 3 — CONFRONTO DINAMICO PESO IN BYTE (JSON vs MD vs XML)
  // --------------------------------------------------------------------------
  const datasetsP3 = {
    iot: {
      json: `{\n  "id": "ESP32_01",\n  "stanza": "GamingRoom",\n  "temp": 24.5,\n  "fan": true,\n  "allarmi": ["temp_alta", "led_on"]\n}`,
      md: `# Telemetria ESP32_01\n- **Stanza**: GamingRoom\n- **Temp**: \`24.5 °C\`\n- **Ventola**: Attiva\n\n| Allarmi |\n| --- |\n| temp_alta |\n| led_on |`,
      xml: `<?xml version="1.0" encoding="UTF-8"?>\n<telemetria>\n  <id>ESP32_01</id>\n  <stanza>GamingRoom</stanza>\n  <temp unita="C">24.5</temp>\n  <fan>true</fan>\n  <allarmi>\n    <allarme>temp_alta</allarme>\n    <allarme>led_on</allarme>\n  </allarmi>\n</telemetria>`
    },
    player: {
      json: `{\n  "nick": "Alex_5A",\n  "livello": 50,\n  "hardcore": false,\n  "oggetti": ["Spada", "Arco", "Scudo"]\n}`,
      md: `## Giocatore: Alex_5A\n- Livello: **50**\n- Modalità: *Normale*\n- Inventario: Spada, Arco, Scudo`,
      xml: `<?xml version="1.0" encoding="UTF-8"?>\n<player>\n  <nick>Alex_5A</nick>\n  <livello>50</livello>\n  <hardcore>false</hardcore>\n  <oggetti>\n    <item>Spada</item>\n    <item>Arco</item>\n    <item>Scudo</item>\n  </oggetti>\n</player>`
    },
    alert: {
      json: `{\n  "server": "Web_TPSIT",\n  "codice": 503,\n  "dettaglio": "CPU al **95%**"\n}`,
      md: `### ⚠️ Allarme Web_TPSIT\nCodice: **503**\nDettaglio: CPU al **95%**`,
      xml: `<?xml version="1.0" encoding="UTF-8"?>\n<allarme>\n  <server>Web_TPSIT</server>\n  <codice>503</codice>\n  <dettaglio>CPU al 95%</dettaglio>\n</allarme>`
    }
  };

  const p3SelectDataset = document.getElementById('p3SelectDataset');
  const p3ByteJson = document.getElementById('p3ByteJson');
  const p3ByteMd = document.getElementById('p3ByteMd');
  const p3ByteXml = document.getElementById('p3ByteXml');
  const p3CodeJson = document.getElementById('p3CodeJson');
  const p3CodeMd = document.getElementById('p3CodeMd');
  const p3CodeXml = document.getElementById('p3CodeXml');
  const p3SavingBanner = document.getElementById('p3SavingBanner');

  function updateP3Comparison() {
    if (!p3SelectDataset) return;
    const key = p3SelectDataset.value;
    const item = datasetsP3[key];

    const jsonBytes = new Blob([item.json]).size;
    const mdBytes = new Blob([item.md]).size;
    const xmlBytes = new Blob([item.xml]).size;

    p3CodeJson.textContent = item.json;
    p3CodeMd.textContent = item.md;
    p3CodeXml.textContent = item.xml;

    p3ByteJson.textContent = `${jsonBytes} byte`;
    p3ByteMd.textContent = `${mdBytes} byte`;
    p3ByteXml.textContent = `${xmlBytes} byte`;

    const savedPct = Math.round(((xmlBytes - jsonBytes) / xmlBytes) * 100);
    p3SavingBanner.innerHTML =
      `📊 <strong>Verifica di Rete</strong>: Il pacchetto <strong>JSON (${jsonBytes} byte)</strong> fa risparmiare il <strong>${savedPct}% di byte trasmessi</strong> rispetto a <strong>XML (${xmlBytes} byte)</strong> eliminando tutti i tag di chiusura ridondanti. <strong>Markdown (${mdBytes} byte)</strong> non è utilizzabile da un parser automatico Java ma serve per i report a video.`;
  }

  if (p3SelectDataset) {
    p3SelectDataset.addEventListener('change', updateP3Comparison);
    updateP3Comparison();
  }

  // --------------------------------------------------------------------------
  // 9. MINI-TEST 3.1 & 3.2 (PARTE 3)
  // --------------------------------------------------------------------------
  const btnCheckMt5 = document.getElementById('btnCheckMiniTest5');
  const feedbackMt5 = document.getElementById('feedbackMiniTest5');
  if (btnCheckMt5 && feedbackMt5) {
    btnCheckMt5.addEventListener('click', () => {
      const selected = document.querySelector('input[name="mt5"]:checked');
      feedbackMt5.style.display = 'block';
      if (!selected) {
        feedbackMt5.className = 'mini-test-feedback status-warn';
        feedbackMt5.textContent = '⚠️ Seleziona un\'opzione prima di verificare.';
        return;
      }
      if (selected.value === 'optA') {
        learningState.miniTests.mt5 = true;
        feedbackMt5.className = 'mini-test-feedback status-ok';
        feedbackMt5.innerHTML = '🎉 <strong>Esatto!</strong> Su reti a bassa banda o dispositivi IoT a batteria, il peso dei tag XML (es. <code>&lt;temperatura&gt;24.5&lt;/temperatura&gt;</code>) spreca pacchetti di rete ed energia. JSON offre la massima compattezza senza perdere la tipizzazione.';
      } else {
        feedbackMt5.className = 'mini-test-feedback status-error';
        feedbackMt5.textContent = '❌ Risposta errata. Valuta il consumo di byte in rete e il tempo di elaborazione del parser.';
      }
      updateGlobalProgress();
    });
  }

  const btnCheckMt6 = document.getElementById('btnCheckMiniTest6');
  const feedbackMt6 = document.getElementById('feedbackMiniTest6');
  if (btnCheckMt6 && feedbackMt6) {
    btnCheckMt6.addEventListener('click', () => {
      const selected = document.querySelector('input[name="mt6"]:checked');
      feedbackMt6.style.display = 'block';
      if (!selected) {
        feedbackMt6.className = 'mini-test-feedback status-warn';
        feedbackMt6.textContent = '⚠️ Seleziona un\'opzione prima di verificare.';
        return;
      }
      if (selected.value === 'optB') {
        learningState.miniTests.mt6 = true;
        feedbackMt6.className = 'mini-test-feedback status-ok';
        feedbackMt6.innerHTML = '🎉 <strong>Eccellente!</strong> XML è ancora fondamentale nell\'ecosistema Java (<code>pom.xml</code> per Maven, layout Android e fatturazione elettronica/servizi bancari) proprio perché permette contratti formali rigidi e validati tramite schemi <strong>XSD</strong>.';
      } else {
        feedbackMt6.className = 'mini-test-feedback status-error';
        feedbackMt6.textContent = '❌ Risposta errata. XML non è adatto per messaggi istantanei leggeri né per guide umane, ma per configurazioni formali complesse.';
      }
      updateGlobalProgress();
    });
  }

  // --------------------------------------------------------------------------
  // 10. SFIDA FINALE DEI 3 CASI REALI SIMULATI (VERIFICA AUTENTICA)
  // --------------------------------------------------------------------------

  // Caso Reale 1
  const btnVerifyC1 = document.getElementById('btnVerifyC1');
  const c1Editor = document.getElementById('c1CodeEditor');
  const c1Feedback = document.getElementById('c1Feedback');
  const badgeC1 = document.getElementById('badgeC1');

  if (btnVerifyC1 && c1Editor && c1Feedback && badgeC1) {
    btnVerifyC1.addEventListener('click', () => {
      const format = document.getElementById('c1FormatSelect').value;
      const checks = Array.from(document.querySelectorAll('.c1-check')).every((c) => c.checked);

      if (format !== 'json') {
        c1Feedback.className = 'status-banner status-error';
        c1Feedback.textContent = '❌ Passo 1: Selezione non corretta. Quale formato è strutturato per un server Java e non consuma banda inutile?';
        return;
      }
      if (!checks) {
        c1Feedback.className = 'status-banner status-warn';
        c1Feedback.textContent = '⚠️ Passo 2: Spunta tutte le voci della checklist di ispezione prima di validare.';
        return;
      }

      try {
        const obj = JSON.parse(c1Editor.value);
        if (typeof obj.stanza !== 'string') {
          c1Feedback.className = 'status-banner status-error';
          c1Feedback.textContent = '❌ La chiave "stanza" deve essere tra doppi apici e contenere una stringa.';
          return;
        }
        if (typeof obj.tempCelsius !== 'number') {
          c1Feedback.className = 'status-banner status-error';
          c1Feedback.textContent = '❌ Anomalia di tipo Java: "tempCelsius" è ancora una stringa! Il server Java attende un double (es. 24.5 senza virgolette e senza °C).';
          return;
        }
        if (typeof obj.ledAttivi !== 'boolean') {
          c1Feedback.className = 'status-banner status-error';
          c1Feedback.textContent = '❌ Anomalia di tipo: "ledAttivi" deve essere un booleano minuscolo (true o false).';
          return;
        }

        learningState.cases.c1 = true;
        badgeC1.textContent = '✅ SUPERATO';
        badgeC1.style.background = 'var(--accent-green)';
        badgeC1.style.color = '#fff';
        c1Feedback.className = 'status-banner status-ok';
        c1Feedback.textContent = '🎉 CASO REALE 1 COMPLETATO! Hai scelto JSON e riparato tutte le anomalie di sintassi e tipo per il backend Java.';
        updateGlobalProgress();
      } catch (err) {
        c1Feedback.className = 'status-banner status-error';
        c1Feedback.textContent = `❌ Errore Sintattico JSON: ${err.message}. Controlla virgolette, virgola finale e maiuscole.`;
      }
    });
  }

  // Caso Reale 2
  const btnVerifyC2 = document.getElementById('btnVerifyC2');
  const c2Editor = document.getElementById('c2CodeEditor');
  const c2Feedback = document.getElementById('c2Feedback');
  const badgeC2 = document.getElementById('badgeC2');

  if (btnVerifyC2 && c2Editor && c2Feedback && badgeC2) {
    btnVerifyC2.addEventListener('click', () => {
      const format = document.getElementById('c2FormatSelect').value;
      const checks = Array.from(document.querySelectorAll('.c2-check')).every((c) => c.checked);
      const raw = c2Editor.value;

      if (format !== 'md') {
        c2Feedback.className = 'status-banner status-error';
        c2Feedback.textContent = '❌ Passo 1: Quale formato è lo standard aperto per i file README letti dagli sviluppatori su GitHub?';
        return;
      }
      if (!checks) {
        c2Feedback.className = 'status-banner status-warn';
        c2Feedback.textContent = '⚠️ Passo 2: Spunta tutte le voci della checklist di ispezione.';
        return;
      }

      const issues = [];
      if (!/^#\s+Guida/m.test(raw)) {
        issues.push('Inserisci lo spazio dopo il cancelletto nel titolo (# Guida Bot Discord 5A).');
      }
      if (!/\*\*Java\*\*/.test(raw)) {
        issues.push('Chiudi i doppi asterischi su **Java**.');
      }
      const fences = (raw.match(/```/g) || []).length;
      if (fences < 2 || fences % 2 !== 0) {
        issues.push('Chiudi il blocco di codice Java con tre backtick ```.');
      }
      if (!/\|\s*---+\s*\|\s*---+\s*\|/.test(raw)) {
        issues.push('Aggiungi la riga separatrice | --- | --- | sotto l\'intestazione della tabella.');
      }
      if (!/\[Repository Ufficiale\]\(https:\/\/github\.com\/classe5\/bot\)/.test(raw)) {
        issues.push('Correggi il link finale nel formato [Repository Ufficiale](https://github.com/classe5/bot).');
      }

      if (issues.length > 0) {
        c2Feedback.className = 'status-banner status-error';
        c2Feedback.textContent = '❌ Anomalie Markdown non ancora risolte: ' + issues.join(' • ');
        return;
      }

      learningState.cases.c2 = true;
      badgeC2.textContent = '✅ SUPERATO';
      badgeC2.style.background = 'var(--accent-green)';
      badgeC2.style.color = '#fff';
      c2Feedback.className = 'status-banner status-ok';
      c2Feedback.textContent = '🎉 CASO REALE 2 COMPLETATO! Il file README.md adesso rispetta lo standard CommonMark ed è renderizzato alla perfezione su GitHub.';
      updateGlobalProgress();
    });
  }

  // Caso Reale 3
  const btnVerifyC3 = document.getElementById('btnVerifyC3');
  const c3Editor = document.getElementById('c3CodeEditor');
  const c3Feedback = document.getElementById('c3Feedback');
  const badgeC3 = document.getElementById('badgeC3');

  if (btnVerifyC3 && c3Editor && c3Feedback && badgeC3) {
    btnVerifyC3.addEventListener('click', () => {
      const xmlChoice = document.getElementById('c3XmlSelect').value;
      const hybridChoice = document.getElementById('c3HybridSelect').value;
      const checks = Array.from(document.querySelectorAll('.c3-check')).every((c) => c.checked);

      if (xmlChoice !== 'pom') {
        c3Feedback.className = 'status-banner status-error';
        c3Feedback.textContent = '❌ Passo 1A: In un progetto Java, XML è lo standard per pom.xml di Maven e per contratti rigorosi validati con XSD.';
        return;
      }
      if (hybridChoice !== 'hybrid') {
        c3Feedback.className = 'status-banner status-error';
        c3Feedback.textContent = '❌ Passo 1B: Osserva il webhook: l\'involucro di trasporto HTTP è JSON, mentre il messaggio dentro "content" usa Markdown.';
        return;
      }
      if (!checks) {
        c3Feedback.className = 'status-banner status-warn';
        c3Feedback.textContent = '⚠️ Passo 2: Spunta entrambe le voci della checklist di ispezione.';
        return;
      }

      try {
        const obj = JSON.parse(c3Editor.value);
        if (typeof obj.tts !== 'boolean') {
          c3Feedback.className = 'status-banner status-error';
          c3Feedback.textContent = '❌ Il campo "tts" deve essere un booleano minuscolo (false).';
          return;
        }
        if (typeof obj.content !== 'string' || !/\*\*Torneo Minecraft\*\*/.test(obj.content)) {
          c3Feedback.className = 'status-banner status-error';
          c3Feedback.textContent = '❌ Anomalia Markdown nel JSON: chiudi il grassetto con doppi asterischi su **Torneo Minecraft** dentro "content".';
          return;
        }

        learningState.cases.c3 = true;
        badgeC3.textContent = '✅ SUPERATO';
        badgeC3.style.background = 'var(--accent-green)';
        badgeC3.style.color = '#fff';
        c3Feedback.className = 'status-banner status-ok';
        c3Feedback.textContent = '🎉 CASO REALE 3 COMPLETATO! Hai padroneggiato il ruolo di XML in Java e la collaborazione tra JSON e Markdown nei webhook.';
        updateGlobalProgress();
      } catch (err) {
        c3Feedback.className = 'status-banner status-error';
        c3Feedback.textContent = `❌ Errore Sintattico nel Payload: ${err.message}. Aggiungi la virgola dopo "TorneoBot_5A"!`;
      }
    });
  }

  // --------------------------------------------------------------------------
  // 11. CONSEGNA A GOOGLE WORKSPACE (@SCUOLA.EDU.IT) & DOWNLOAD REPORT
  // --------------------------------------------------------------------------
  function buildFinalReport() {
    const mtPassed = Object.values(learningState.miniTests).filter(Boolean).length;
    const casesPassed = Object.values(learningState.cases).filter(Boolean).length;
    const email = document.getElementById('finalStudentEmail')?.value.trim() || 'non_indicata@scuola.edu.it';
    const names = document.getElementById('finalStudentNames')?.value.trim() || 'Coppia Classe 5A';

    return {
      timestamp: new Date().toISOString(),
      email: email,
      studentNames: names,
      miniTestsPassed: mtPassed,
      miniTestsTotal: 6,
      case1Passed: learningState.cases.c1,
      case2Passed: learningState.cases.c2,
      case3Passed: learningState.cases.c3,
      casesCompletedCount: casesPassed,
      allCasesSuccessful: casesPassed === 3,
      elapsedSeconds: Math.round((Date.now() - sessionStartTime) / 1000)
    };
  }

  const btnSubmit = document.getElementById('btnSubmitFinalWorkspace');
  const bannerSubmit = document.getElementById('finalSubmitStatusBanner');
  if (btnSubmit && bannerSubmit) {
    btnSubmit.addEventListener('click', async () => {
      const payload = buildFinalReport();
      const webhookUrl = document.getElementById('finalTeacherWebhookUrl')?.value.trim();

      bannerSubmit.style.display = 'block';

      if (!webhookUrl) {
        bannerSubmit.className = 'status-banner status-warn';
        bannerSubmit.innerHTML =
          `ℹ️ <strong>Modalità Locale</strong>: Nessun URL webhook inserito. ` +
          `Report registrato per <strong>${payload.studentNames}</strong> (${payload.email}): ` +
          `<strong>${payload.miniTestsPassed}/6 Mini-Test</strong> e <strong>${payload.casesCompletedCount}/3 Casi Reali Superati</strong>. ` +
          `Clicca su "Scarica Report (.json)" per consegnare il file su Classroom!`;
        return;
      }

      try {
        bannerSubmit.className = 'status-banner status-ok';
        bannerSubmit.textContent = '⏳ Invio in corso al Google Sheet della classe...';

        await fetch(webhookUrl, {
          method: 'POST',
          mode: 'no-cors',
          headers: { 'Content-Type': 'text/plain;charset=utf-8' },
          body: JSON.stringify(payload)
        });

        bannerSubmit.className = 'status-banner status-ok';
        bannerSubmit.innerHTML =
          `✅ <strong>Invio Eseguito con Successo su Google Workspace!</strong> Studente/Coppia: <strong>${payload.studentNames}</strong> (${payload.email}) — ` +
          `Esito: <strong>${payload.casesCompletedCount}/3 Casi Reali Superati</strong> (Tempo: ${payload.elapsedSeconds}s).`;
      } catch (err) {
        bannerSubmit.className = 'status-banner status-error';
        bannerSubmit.textContent = '❌ Errore durante l\'invio: ' + err.message;
      }
    });
  }

  const btnDownloadReport = document.getElementById('btnDownloadJsonReport');
  if (btnDownloadReport) {
    btnDownloadReport.addEventListener('click', () => {
      const payload = buildFinalReport();
      const blob = new Blob([JSON.stringify(payload, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `attestato_tpsit_${payload.studentNames.replace(/[^a-zA-Z0-9]/g, '_')}.json`;
      a.click();
      URL.revokeObjectURL(url);
    });
  }

  updateGlobalProgress();
});
