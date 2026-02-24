// src/main/resources/static/js/main-course.js

let currentOrder = 'latest';
let debounceTimer = null;

const REGION_MAP = {
  "서울특별시": ["서울"],
  "인천광역시": ["인천"],
  "부산광역시": ["부산"],
  "대구광역시": ["대구"],
  "대전광역시": ["대전"],
  "광주광역시": ["광주"],
  "울산광역시": ["울산"],
  "세종특별자치시": ["세종"],
  "제주특별자치도": ["제주", "서귀포"],
  "경기도": ["가평", "고양", "과천", "광명", "광주", "구리", "군포", "김포", "남양주", "동두천", "부천", "성남", "수원", "시흥", "안산", "안성", "안양", "양주", "여주", "오산", "용인", "의왕", "의정부", "이천", "파주", "평택", "포천", "하남", "화성"],
  "강원특별자치도": ["강릉", "고성", "동해", "삼척", "속초", "양양", "원주", "인제", "정선", "춘천", "태백", "평창", "홍천"],
  "충청북도": ["제천", "청주", "충주"],
  "충청남도": ["공주", "논산", "당진", "보령", "부여", "서산", "아산", "천안", "태안"],
  "전라북도": ["군산", "김제", "남원", "익산", "전주", "정읍"],
  "전라남도": ["광양", "나주", "목포", "무안", "보성", "순천", "여수", "완도", "해남"],
  "경상북도": ["경주", "구미", "김천", "문경", "안동", "영주", "영천", "포항"],
  "경상남도": ["거제", "김해", "밀양", "사천", "양산", "진주", "창원", "통영"]
};

const THEME_LABEL = {
  FOOD: "맛집",
  NATURE: "자연",
  CITY: "도시",
  CULTURE: "문화",
  NIGHT: "야경"
};

const LANG_LABEL = {
  KOREAN: "한국어",
  ENGLISH: "영어",
  JAPANESE: "일본어"
};

document.addEventListener('DOMContentLoaded', () => {
  initRegionSelects();
  bindFilterEvents();
  applyFilter();
});

function setOrder(order) {
  currentOrder = order;

  const latestBtn = document.getElementById('latestBtn');
  const popularBtn = document.getElementById('popularBtn');

  if (latestBtn) latestBtn.classList.toggle('active', order === 'latest');
  if (popularBtn) popularBtn.classList.toggle('active', order === 'popular');

  applyFilter();
}

function bindFilterEvents() {
  const provinceEl = document.getElementById('province');
  const regionEl = document.getElementById('region');
  const themeEl = document.getElementById('theme');

  if (provinceEl) {
    provinceEl.addEventListener('change', () => {
      fillCities(provinceEl.value || '');
      applyFilter();
    });
  }

  if (regionEl) regionEl.addEventListener('change', applyFilter);
  if (themeEl) themeEl.addEventListener('change', applyFilter);

  document.querySelectorAll('.lang')
      .forEach(cb => cb.addEventListener('change', applyFilter));
}

function applyFilter() {
  clearTimeout(debounceTimer);
  debounceTimer = setTimeout(fetchFilteredTrips, 150);
}

function fetchFilteredTrips() {
  const region = document.getElementById('region')?.value || '';
  const theme = document.getElementById('theme')?.value || '';

  const languages = Array.from(document.querySelectorAll('.lang:checked'))
      .map(cb => cb.value);

  const qs = new URLSearchParams();
  qs.append('order', currentOrder);

  if (region) qs.append('region', region);
  if (theme) qs.append('theme', theme);
  languages.forEach(lang => qs.append('languages', lang));

  const url = `/api/trips/mainList?${qs.toString()}`;

  fetch(url, { credentials: 'include' })
      .then(res => res.json())
      .then(data => renderTrips(data.content || data))
      .catch(showError);
}

function renderTrips(trips) {
  const container = document.getElementById('tripList');
  if (!container) return;

  container.innerHTML = '';

  if (!trips || trips.length === 0) {
    container.innerHTML = `<div class="empty">등록된 여정이 없습니다.</div>`;
    return;
  }

  trips.forEach(trip => {
    const article = document.createElement('article');
    article.className = 'trip-card';
    article.onclick = () => {
      location.href = `/trip/detail/${trip.tripId}`;
    };

    const st = (trip.status && typeof trip.status === 'string')
        ? trip.status
        : (trip.status && trip.status.name ? trip.status.name : '');

    const statusClass = st === 'OPEN'
        ? ' tl-pill--open'
        : (st === 'IN_PROGRESS'
            ? ' tl-pill--progress'
            : (st === 'CLOSED'
                ? ' tl-pill--closed'
                : (st === 'FINISHED'
                    ? ' tl-pill--finished'
                    : ' tl-pill--draft')));

    const statusText = st === 'DRAFT'
        ? '임시저장'
        : (st === 'OPEN'
            ? '모집중'
            : (st === 'CLOSED'
                ? '모집마감'
                : (st === 'IN_PROGRESS'
                    ? '진행중'
                    : (st === 'FINISHED'
                        ? '종료됨'
                        : '-'))));

    const regionChip = escapeHtml(trip.region ?? '지역 미정');

    const langCodes = Array.isArray(trip.languageCodes) ? trip.languageCodes : [];
    const langChipsHtml = langCodes.map(code => {
      const key = String(code || '').toUpperCase();
      const label = LANG_LABEL[key] || key;
      return `<span class="tl-chip">${escapeHtml(label)}</span>`;
    }).join('');

    const themeKey = String(trip.theme || '').toUpperCase();
    const themeLabel = THEME_LABEL[themeKey] || (trip.theme ?? '-');
    const themeChip = `<span class="tl-chip">${escapeHtml(themeLabel)}</span>`;

    const current = Number(trip.currentParticipants ?? 0);
    const max = trip.maxParticipants ?? '-';

    const likedByMe = Boolean(trip.likedByMe);
    const likeCount = Number(trip.likeCount ?? 0);

    const hostDeleted = Boolean(trip.hostDeleted);
    const hostName = trip.hostName ?? '호스트';
    const hostUserId = trip.hostUserId ?? '';

    article.innerHTML = `
      <div class="trip-card__body">
        <div class="trip-card__title-row">
          <div class="trip-card__title">${escapeHtml(trip.title ?? '제목')}</div>

          <div class="trip-card__status">
            <span class="tl-pill${statusClass}">
              <span>${escapeHtml(statusText)}</span>
            </span>
          </div>
        </div>

        <div class="trip-card__meta">
          <span class="tl-chip">${regionChip}</span>
          ${langChipsHtml}
          ${themeChip}
          <span class="dot">•</span>
          <span class="tl-people">${escapeHtml(String(current))}/${escapeHtml(String(max))}</span>
        </div>

        <div class="trip-card__desc">${escapeHtml(trip.description ?? '')}</div>
      </div>

      <div class="trip-card__footer" onclick="event.stopPropagation();">
        <button class="like-btn"
                type="button"
                data-like-btn
                data-trip-id="${escapeHtml(String(trip.tripId))}"
                data-liked="${likedByMe}"
                data-count="${likeCount}">
          <span class="like-btn__icon" data-like-icon>${likedByMe ? '♥' : '♡'}</span>
          <span class="like-btn__count" data-like-count>${escapeHtml(String(likeCount))}</span>
        </button>

        <span class="trip-card__host" onclick="event.stopPropagation();">
          ${hostDeleted
        ? `<span class="hc-host-deleted">탈퇴한 사용자</span>`
        : `<a href="#"
                  class="hc-host-link"
                  data-host-card-open
                  data-host-id="${escapeHtml(String(hostUserId))}">${escapeHtml(hostName)}</a>`
    }
        </span>
      </div>
    `;

    container.appendChild(article);
  });
}

function showError(err) {
  console.error(err);
  const container = document.getElementById('tripList');
  if (container) {
    container.innerHTML = `<div class="empty">데이터를 불러오는 중 오류가 발생했습니다.</div>`;
  }
}

function escapeHtml(s) {
  return String(s ?? "").replace(/[&<>"']/g, (m) => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    "\"": "&quot;",
    "'": "&#39;"
  }[m]));
}

/* =========================
   지역(도 -> 시) 초기화
========================= */
function initRegionSelects() {
  const provinceEl = document.getElementById('province');
  const cityEl = document.getElementById('region');
  if (!provinceEl || !cityEl) return;

  provinceEl.innerHTML = '';
  const allProvince = document.createElement('option');
  allProvince.value = '';
  allProvince.textContent = '전체(도)';
  provinceEl.appendChild(allProvince);

  Object.keys(REGION_MAP).forEach((p) => {
    const opt = document.createElement('option');
    opt.value = p;
    opt.textContent = p;
    provinceEl.appendChild(opt);
  });

  fillCities('');
}

function fillCities(province) {
  const cityEl = document.getElementById('region');
  if (!cityEl) return;

  cityEl.innerHTML = '';
  const allCity = document.createElement('option');
  allCity.value = '';
  allCity.textContent = '전체(시)';
  cityEl.appendChild(allCity);

  if (!province) return;

  const cities = REGION_MAP[province] || [];
  cities.forEach((c) => {
    const opt = document.createElement('option');
    opt.value = c;
    opt.textContent = c;
    cityEl.appendChild(opt);
  });
}