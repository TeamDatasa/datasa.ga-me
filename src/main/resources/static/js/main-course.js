// src/main/resources/static/js/main-course.js

let currentOrder = 'latest';
let debounceTimer = null;

const MAIN_LIMIT = 3;

const REGION_MAP = {
  "ソウル特別市": ["ソウル"],
  "仁川広域市": ["仁川"],
  "釜山広域市": ["釜山"],
  "大邱広域市": ["大邱"],
  "大田広域市": ["大田"],
  "光州広域市": ["光州"],
  "蔚山広域市": ["蔚山"],
  "世宗特別自治市": ["世宗"],
  "済州特別自治道": ["済州", "西帰浦"],
  "京畿道": ["가평", "고양", "과천", "광명", "光州", "구리", "군포", "김포", "남양주", "동두천", "부천", "성남", "수원", "市흥", "안산", "안성", "안양", "양주", "여주", "오산", "용인", "의왕", "의정부", "이천", "파주", "평택", "포천", "하남", "화성"],
  "江原特別自治道": ["강릉", "고성", "동해", "삼척", "속초", "양양", "원주", "인제", "정선", "춘천", "태백", "평창", "홍천"],
  "忠清北道": ["제천", "청주", "충주"],
  "忠清南道": ["공주", "논산", "당진", "보령", "부여", "서산", "아산", "천안", "태안"],
  "全羅北道": ["군산", "김제", "남원", "익산", "전주", "정읍"],
  "全羅南道": ["광양", "나주", "목포", "무안", "보성", "순천", "여수", "완道", "해남"],
  "慶尚北道": ["경주", "구미", "김천", "문경", "안동", "영주", "영천", "포항"],
  "慶尚南道": ["거제", "김해", "밀양", "사천", "양산", "진주", "창원", "통영"]
};

const THEME_LABEL = {
  FOOD: "グルメ",
  NATURE: "自然",
  CITY: "都市",
  CULTURE: "文化",
  NIGHT: "夜景",

  HEALING: "癒やし",
  ACTIVITY: "アクティビティ",
  SHOPPING: "ショッピング",
  HISTORY: "歴史",
  PHOTO: "写真",
  FAMILY: "家族"
};

const LANG_LABEL = {
  KOREAN: "韓国語",
  ENGLISH: "英語",
  JAPANESE: "日本語"
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
  const province = document.getElementById('province')?.value || '';
  const region = document.getElementById('region')?.value || '';
  const theme = document.getElementById('theme')?.value || '';

  const languages = Array.from(document.querySelectorAll('.lang:checked'))
      .map(cb => cb.value);

  const qs = new URLSearchParams();
  qs.append('order', currentOrder);

  if (province) qs.append('province', province);
  if (region) qs.append('region', region);
  if (theme) qs.append('theme', theme);
  languages.forEach(lang => qs.append('languages', lang));

  updateMoreLink(qs.toString());

  const apiQs = new URLSearchParams(qs.toString());
  apiQs.set('page', '0');
  apiQs.set('size', String(MAIN_LIMIT + 1));

  const url = `/api/trips/mainList?${apiQs.toString()}`;

  fetch(url, { credentials: 'include' })
      .then(res => res.json())
      .then(data => renderTrips(data.content || data, qs.toString()))
      .catch(showError);
}

function renderTrips(trips, qs) {
  const container = document.getElementById('tripList');
  if (!container) return;

  container.innerHTML = '';

  if (!trips || trips.length === 0) {
    container.innerHTML = `<div class="empty">登録されたツアーがありません。</div>`;
    toggleMore(false);
    return;
  }

  const limited = trips.slice(0, MAIN_LIMIT);
  toggleMore(trips.length > MAIN_LIMIT);

  limited.forEach(trip => {
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
        ? '下書き'
        : (st === 'OPEN'
            ? '募集中'
            : (st === 'CLOSED'
                ? '募集終了'
                : (st === 'IN_PROGRESS'
                    ? '進行中'
                    : (st === 'FINISHED'
                        ? '終了'
                        : '-'))));

    const regionChip = escapeHtml(trip.region ?? '地域未定');

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
    const hostName = trip.hostName ?? 'ホスト';
    const hostUserId = trip.hostUserId ?? '';

    article.innerHTML = `
      <div class="trip-card__body">
        <div class="trip-card__title-row">
          <div class="trip-card__title">${escapeHtml(trip.title ?? 'タイトル')}</div>

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
        ? `<span class="hc-host-deleted">退会したユーザー</span>`
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

function updateMoreLink(qs) {
  const moreWrap = document.getElementById('mainMoreWrap');
  const moreLink = document.getElementById('mainMoreLink');
  if (!moreWrap || !moreLink) return;

  moreLink.href = qs ? `/trip/listAll?${qs}` : `/trip/listAll`;
}

function toggleMore(show) {
  const moreWrap = document.getElementById('mainMoreWrap');
  if (!moreWrap) return;
  moreWrap.style.display = show ? 'flex' : 'none';
}

function showError(err) {
  console.error(err);
  const container = document.getElementById('tripList');
  if (container) {
    container.innerHTML = `<div class="empty">データの読み込み中にエラーが発生しました。</div>`;
  }
  toggleMore(false);
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
   地域(道 -> 市) リセット
========================= */
function initRegionSelects() {
  const provinceEl = document.getElementById('province');
  const cityEl = document.getElementById('region');
  if (!provinceEl || !cityEl) return;

  provinceEl.innerHTML = '';
  const allProvince = document.createElement('option');
  allProvince.value = '';
  allProvince.textContent = 'すべて（道・広域）';
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
  allCity.textContent = 'すべて（市）';
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