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
  "경기도": ["가평", "김포", "남양주", "동두천", "부천", "시흥", "안산", "안성", "안양", "오산", "파주", "성남", "수원", "용인", "하남", "화성", "연천"],
  "강원특별자치도": ["강릉", "고성", "동해", "속초", "양양", "영월", "원주", "정선", "춘천"],
  "충청북도": ["제천", "청주", "충주"],
  "충청남도": ["공주", "논산", "당진", "보령", "부여", "서산", "천안"],
  "전라북도": ["군산", "익산", "전주"],
  "전라남도": ["광양", "나주", "목포", "무안", "보성", "순천", "여수", "완도"],
  "경상북도": ["경주", "구미", "김천", "안동", "영주", "영천", "포항", "상주"],
  "경상남도": ["거제", "김해", "밀양", "사천", "창원", "통영", "양산"]
};

const THEME_LABEL = {
  FOOD: "맛집",
  NATURE: "자연",
  CITY: "도시",
  CULTURE: "문화",
  NIGHT: "야경"
};

document.addEventListener('DOMContentLoaded', () => {
  initRegionSelects();
  bindFilterEvents();
  applyFilter();
});

function setOrder(order) {
  currentOrder = order;

  document.getElementById('latestBtn')
      ?.classList.toggle('active', order === 'latest');
  document.getElementById('popularBtn')
      ?.classList.toggle('active', order === 'popular');

  applyFilter();
}

function bindFilterEvents() {
  document.getElementById('province')
      ?.addEventListener('change', () => {
        const province = document.getElementById('province')?.value || '';
        fillCities(province);
        applyFilter();
      });

  document.getElementById('region')
      ?.addEventListener('change', applyFilter);

  document.getElementById('theme')
      ?.addEventListener('change', applyFilter);

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

  const languages = Array.from(
      document.querySelectorAll('.lang:checked')
  ).map(cb => cb.value);

  const qs = new URLSearchParams();
  qs.append('order', currentOrder);

  if (region) qs.append('region', region);
  if (theme) qs.append('theme', theme);
  languages.forEach(lang => qs.append('languages', lang));

  const url = `/api/trips/mainList?${qs.toString()}`;

  fetch(url)
      .then(res => res.json())
      .then(data => renderTrips(data.content || data))
      .catch(showError);
}

function renderTrips(trips) {
  const container = document.getElementById('tripList');
  if (!container) return;

  container.innerHTML = '';

  if (!trips || trips.length === 0) {
    container.innerHTML = `<p class="yw-muted">표시할 프로젝트가 없습니다.</p>`;
    return;
  }

  trips.forEach(trip => {
    const themeLabel = THEME_LABEL[String(trip.theme || '').toUpperCase()] || (trip.theme ?? '-');

    const card = document.createElement('article');
    card.className = 'yw-card';
    card.onclick = () => {
      location.href = `/trip/detail/${trip.tripId}`;
    };

    card.innerHTML = `
      <div class="yw-card__thumb"></div>
      <div class="yw-card__body">
        <div class="yw-card__title">${escapeHtml(trip.title)}</div>
        <div class="yw-card__meta">
          <span>${escapeHtml(trip.region)}</span>
          <span class="yw-dot">•</span>
          <span>${escapeHtml(themeLabel)}</span>
        </div>
        <div class="yw-card__meta muted">
          모집 ${Number(trip.approvedCount ?? 0) + 1}/ ${escapeHtml(trip.maxParticipants)}
        </div>
      </div>
    `;

    container.appendChild(card);
  });
}

function showError(err) {
  console.error(err);
  const container = document.getElementById('tripList');
  if (container) {
    container.innerHTML = `<p style="color:red">데이터를 불러오는 중 오류가 발생했습니다.</p>`;
  }
}

function escapeHtml(s) {
  return String(s ?? "")
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#039;");
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