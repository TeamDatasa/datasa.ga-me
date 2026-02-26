// src/main/resources/static/js/trip-list-filter.js

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

document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('listFilterForm');
  if (!form) return;

  const provinceEl = document.getElementById('province');
  const regionEl = document.getElementById('region');
  const themeEl = document.getElementById('theme');
  const orderInput = document.getElementById('orderInput');

  const selectedProvince = form.getAttribute('data-selected-province') || '';
  const selectedRegion = form.getAttribute('data-selected-region') || '';
  const selectedOrder = form.getAttribute('data-selected-order') || 'latest';

  initProvince(provinceEl, selectedProvince);
  fillCities(regionEl, selectedProvince, selectedRegion);

  bindAutoSubmit(form, provinceEl, regionEl, themeEl);
  bindOrderButtons(form, orderInput, selectedOrder);
});

function bindAutoSubmit(form, provinceEl, regionEl, themeEl) {
  if (provinceEl) {
    provinceEl.addEventListener('change', () => {
      fillCities(regionEl, provinceEl.value || '', '');
      form.submit();
    });
  }

  if (regionEl) regionEl.addEventListener('change', () => form.submit());
  if (themeEl) themeEl.addEventListener('change', () => form.submit());

  document.querySelectorAll('.lang')
      .forEach(cb => cb.addEventListener('change', () => form.submit()));
}

function bindOrderButtons(form, orderInput, selectedOrder) {
  const latestBtn = document.getElementById('latestBtn');
  const popularBtn = document.getElementById('popularBtn');

  setActive(latestBtn, selectedOrder !== 'popular');
  setActive(popularBtn, selectedOrder === 'popular');

  if (latestBtn) {
    latestBtn.addEventListener('click', () => {
      if (orderInput) orderInput.value = 'latest';
      form.submit();
    });
  }

  if (popularBtn) {
    popularBtn.addEventListener('click', () => {
      if (orderInput) orderInput.value = 'popular';
      form.submit();
    });
  }
}

function setActive(btn, active) {
  if (!btn) return;
  btn.classList.toggle('active', Boolean(active));
}

function initProvince(provinceEl, selectedProvince) {
  if (!provinceEl) return;

  const keepFirst = provinceEl.querySelector('option[value=""]');
  provinceEl.innerHTML = '';
  if (keepFirst) provinceEl.appendChild(keepFirst);

  Object.keys(REGION_MAP).forEach((p) => {
    const opt = document.createElement('option');
    opt.value = p;
    opt.textContent = p;
    if (p === selectedProvince) opt.selected = true;
    provinceEl.appendChild(opt);
  });
}

function fillCities(regionEl, province, selectedRegion) {
  if (!regionEl) return;

  const keepFirst = regionEl.querySelector('option[value=""]');
  regionEl.innerHTML = '';
  if (keepFirst) regionEl.appendChild(keepFirst);

  if (!province) return;

  const cities = REGION_MAP[province] || [];
  cities.forEach((c) => {
    const opt = document.createElement('option');
    opt.value = c;
    opt.textContent = c;
    if (c === selectedRegion) opt.selected = true;
    regionEl.appendChild(opt);
  });
}