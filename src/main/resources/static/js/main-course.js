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
