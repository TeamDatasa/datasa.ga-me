/**
 * main.js
 * - 메인화면 필터 + 리스트 전용
 * - AI 추천 / 필터 리스트 분리 가능
 */

let currentOrder = 'latest';
let debounceTimer = null;

document.addEventListener('DOMContentLoaded', () => {
  bindFilterEvents();   
  applyFilter(); 
  loadAiRecommend(); // AI 추천 

});

/* ===============================
   정렬 버튼
================================ */
function setOrder(order) {
  currentOrder = order;

  document.getElementById('latestBtn')
    ?.classList.toggle('active', order === 'latest');
  document.getElementById('popularBtn')
    ?.classList.toggle('active', order === 'popular');

  applyFilter();
}

/* ===============================
   기본 목록 로드
================================ */
function loadTrips() {
  fetch(`/api/trips?order=${currentOrder}`)
    .then(res => res.json())
    .then(data => renderTrips(data.content || data))
    .catch(showError);
}

function bindFilterEvents() {
  document.getElementById('region')
    ?.addEventListener('change', applyFilter);

  document.getElementById('theme')
    ?.addEventListener('change', applyFilter);

  document.querySelectorAll('.lang')
    .forEach(cb => cb.addEventListener('change', applyFilter));
}

/* ===============================
   필터 적용 (즉시)
================================ */
function applyFilter() {
  // debounce (연속 변경 방지)
  clearTimeout(debounceTimer);
  debounceTimer = setTimeout(fetchFilteredTrips, 150);
}

function fetchFilteredTrips() {
  const region = document.getElementById('region')?.value;
  const theme = document.getElementById('theme')?.value;

  const languages = Array.from(
    document.querySelectorAll('.lang:checked')
  ).map(cb => cb.value);

  const qs = new URLSearchParams();
  qs.append('order', currentOrder);

  if (region) qs.append('region', region);
  if (theme) qs.append('theme', theme);

  // 🔥 언어는 AND + OR 혼합 (같은 key 여러번)
  languages.forEach(lang => qs.append('languages', lang));

  const url = `/api/trips/mainList?${qs.toString()}`;

  fetch(url)
    .then(res => res.json())
    .then(data => renderTrips(data.content || data))
    .catch(showError);
}

/* ===============================
   필터 초기화
================================ */
function resetFilter() {
  document.querySelectorAll('.lang')
    .forEach(cb => cb.checked = false);

  const region = document.getElementById('region');
  const theme = document.getElementById('theme');

  if (region) region.value = '';
  if (theme) theme.value = '';

  setOrder('latest');
}

/* ===============================
   전체 리스트 렌더링
================================ */
function renderTrips(trips) {
  const container = document.getElementById('tripList');
  if (!container) return;

  container.innerHTML = '';

  if (!trips || trips.length === 0) {
    container.innerHTML =
      `<p class="yw-muted">표시할 프로젝트가 없습니다.</p>`;
    return;
  }

  trips.forEach(trip => {
    const card = document.createElement('article');
    card.className = 'yw-card';
    card.onclick = () => {
      location.href = `/trip/detail/${trip.tripId}`;
    };

    card.innerHTML = `
      <div class="yw-card__thumb"></div>
      <div class="yw-card__body">
        <div class="yw-card__title">${trip.title}</div>
        <div class="yw-card__meta">
          <span>${trip.region}</span>
          <span class="yw-dot">•</span>
          <span>${trip.theme}</span>
        </div>
        <div class="yw-card__meta muted">
          모집 ${trip.approvedCount}/ ${trip.maxParticipants}
        </div>
      </div>
    `;

    container.appendChild(card);
  });
}

/* ===============================
   AI 추천 렌더링
================================ */
function loadAiRecommend() {
  const container = document.getElementById('aiRecommendList');
  if (!container) return;

  fetch('/api/trips/recommend/weekly')
    .then(res => res.json())
    .then(data => {
      container.innerHTML = '';

      if (!data || data.length === 0) {
        container.innerHTML =
          `<p class="yw-muted">추천 데이터 준비 중</p>`;
        return;
      }

      data.forEach(trip => {
        const card = document.createElement('article');
        card.className = 'yw-card';
        card.onclick = () => {
          location.href = `/trip/detail/${trip.tripId}`;
        };

        card.innerHTML = `
          <div class="yw-card__thumb"></div>
          <div class="yw-card__body">
            <div class="yw-card__title">${trip.title}</div>
            <div class="yw-card__meta">
              <span>${trip.region}</span>
              <span class="yw-dot">•</span>
              <span>AI 추천</span>
            </div>
          </div>
        `;
        container.appendChild(card);
      });
    })
    .catch(err => {
      console.error(err);
      container.innerHTML =
        `<p class="yw-muted">추천 로딩 실패</p>`;
    });
}

/* ===============================
   에러 처리
================================ */
function showError(err) {
  console.error(err);
  const container = document.getElementById('tripList');
  if (container) {
    container.innerHTML =
      `<p style="color:red">데이터를 불러오는 중 오류가 발생했습니다.</p>`;
  }
}

document.addEventListener('DOMContentLoaded', () => {
  bindFilterEvents();
  applyFilter();
  loadAiRecommend();

  bindRecommendationButton(); // ✅ 추가
});

/* ===============================
   추천받기 버튼 바인딩
================================ */
function bindRecommendationButton() {
  // 예니 버튼에 id 달면 제일 안정적: id="recBtn"
  const btn =
    document.getElementById('recBtn') ||
    document.querySelector('a[href="/recommendations"]');

  if (!btn) return;

  btn.addEventListener('click', async (e) => {
    e.preventDefault(); // ✅ 페이지 이동 막음

    const overlay = openRecommendationOverlay();
    const body = overlay.querySelector('.rec-body');
    body.innerHTML = `<p class="yw-muted">Loading...</p>`;

    try {
      const res = await fetch('/api/recommendations', { credentials: 'include' });
      if (!res.ok) throw new Error(`Failed (${res.status})`);

      const list = await res.json();

      if (!Array.isArray(list) || list.length === 0) {
        body.innerHTML = `<p class="yw-muted">추천 데이터 준비 중</p>`;
        return;
      }

      body.innerHTML = '';
      list.forEach(trip => body.appendChild(buildRecCard(trip)));
    } catch (err) {
      console.error(err);
      body.innerHTML = `<p class="yw-muted">추천 로딩 실패</p>`;
    }
  });
}

/* ===============================
   추천 오버레이 UI
================================ */
function openRecommendationOverlay() {
  let overlay = document.getElementById('recOverlay');
  if (overlay) {
    overlay.classList.remove('is-hidden');
    return overlay;
  }

  overlay = document.createElement('div');
  overlay.id = 'recOverlay';
  overlay.className = 'rec-overlay';

  overlay.innerHTML = `
    <div class="rec-panel" role="dialog" aria-modal="true">
      <div class="rec-top">
        <div class="rec-title">AI 추천</div>
        <button class="rec-close" type="button" aria-label="Close">×</button>
      </div>
      <div class="rec-body"></div>
    </div>
  `;

  // 닫기
  overlay.addEventListener('click', (e) => {
    if (e.target === overlay) overlay.classList.add('is-hidden');
  });
  overlay.querySelector('.rec-close').addEventListener('click', () => {
    overlay.classList.add('is-hidden');
  });

  injectRecStyleOnce();
  document.body.appendChild(overlay);
  return overlay;
}

/* ===============================
   추천 카드 DOM
================================ */
function buildRecCard(trip) {
  const card = document.createElement('article');
  card.className = 'yw-card rec-card';
  card.onclick = () => {
    // 너 기존 상세 url이 /trip/detail/{tripId} 이거니까 그대로
    location.href = `/trip/detail/${trip.tripId}`;
  };

  card.innerHTML = `
    <div class="yw-card__thumb"></div>
    <div class="yw-card__body">
      <div class="yw-card__title">${escapeHtml(trip.title ?? 'Untitled')}</div>
      <div class="yw-card__meta">
        <span>${escapeHtml(trip.region ?? '-')}</span>
        <span class="yw-dot">•</span>
        <span>AI 추천</span>
      </div>
      ${trip.theme ? `<div class="yw-card__meta muted">${escapeHtml(trip.theme)}</div>` : ``}
    </div>
  `;
  return card;
}

function escapeHtml(s) {
  return String(s).replace(/[&<>"']/g, (m) => ({
    "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"
  }[m]));
}

/* ===============================
   오버레이 스타일 (CSS 파일 추가 없이)
================================ */
function injectRecStyleOnce() {
  if (document.getElementById('recOverlayStyle')) return;

  const style = document.createElement('style');
  style.id = 'recOverlayStyle';
  style.textContent = `
    .rec-overlay{
      position:fixed; inset:0;
      background:rgba(0,0,0,.35);
      display:flex; align-items:center; justify-content:center;
      z-index:9999; padding:16px;
    }
    .rec-overlay.is-hidden{ display:none; }
    .rec-panel{
      width:min(980px,100%);
      max-height:min(82vh,760px);
      overflow:auto;
      background:rgba(255,255,255,.88);
      backdrop-filter: blur(10px);
      border:1px solid rgba(0,0,0,.08);
      border-radius:18px;
      box-shadow:0 18px 60px rgba(0,0,0,.18);
    }
    .rec-top{
      display:flex; align-items:center; justify-content:space-between;
      padding:14px 16px;
      border-bottom:1px solid rgba(0,0,0,.08);
    }
    .rec-title{ font-weight:900; }
    .rec-close{
      border:none; background:transparent;
      font-size:22px; line-height:1;
      cursor:pointer;
      padding:6px 10px;
      border-radius:10px;
    }
    .rec-close:hover{ background:rgba(0,0,0,.06); }
    .rec-body{
      padding:14px 16px;
      display:grid;
      grid-template-columns:repeat(3,1fr);
      gap:12px;
    }
    @media(max-width:900px){ .rec-body{ grid-template-columns:repeat(2,1fr); } }
    @media(max-width:560px){ .rec-body{ grid-template-columns:1fr; } }
  `;
  document.head.appendChild(style);
}
