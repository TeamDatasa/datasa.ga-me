let map, ps, infowindow;
let markers = [];
let schedulePlaces = [];

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
    "京畿道": ["가평", "김포", "남양주", "동두천", "부천", "市흥", "안산", "안성", "안양", "오산", "파주", "성남", "수원", "용인", "하남", "화성", "연천"],
    "江原特別自治道": ["강릉", "고성", "동해", "속초", "양양", "영월", "원주", "정선", "춘천"],
    "忠清北道": ["제천", "청주", "충주"],
    "忠清南道": ["공주", "논산", "당진", "보령", "부여", "서산", "천안"],
    "全羅北道": ["군산", "익산", "전주"],
    "全羅南道": ["광양", "나주", "목포", "무안", "보성", "순천", "여수", "완道"],
    "慶尚北道": ["경주", "구미", "김천", "안동", "영주", "영천", "포항", "상주"],
    "慶尚南道": ["거제", "김해", "밀양", "사천", "창원", "통영", "양산"]
};

document.addEventListener('DOMContentLoaded', function () {
    if (window.kakao && kakao.maps && typeof kakao.maps.load === 'function') {
        kakao.maps.load(function () {
            initKakaoMap();
        });
    }

    initRegionSelects();
    initDateTimeRules();
    initThemeCustom();

    loadSchedulePlacesFromHidden();
    renderSchedulePlaces();

    const errEl = document.getElementById('serverError');
    if (errEl && errEl.dataset && errEl.dataset.message) {
        alert(errEl.dataset.message);
    }

    const form = document.querySelector('form.tw-form');
    if (form) {
        form.addEventListener('submit', function (e) {
            if (!validateOnSubmit()) {
                e.preventDefault();
                e.stopPropagation();
            }
        });
    }
});

function initThemeCustom() {
    const themeEl = document.getElementById('theme');
    const wrapEl = document.getElementById('themeCustomWrap');
    const inputEl = document.getElementById('themeCustom');
    if (!themeEl || !wrapEl || !inputEl) return;

    const apply = () => {
        const isOther = themeEl.value === 'OTHER';
        wrapEl.style.display = isOther ? 'block' : 'none';
        inputEl.required = isOther;

        if (!isOther) {
            inputEl.value = '';
        }
    };

    themeEl.addEventListener('change', apply);
    apply();
}

function initKakaoMap() {
    map = new kakao.maps.Map(document.getElementById('map'), {
        center: new kakao.maps.LatLng(37.566826, 126.9786567),
        level: 3
    });

    ps = new kakao.maps.services.Places(map);
    infowindow = new kakao.maps.InfoWindow({zIndex: 1});
}

function searchPlaces() {
    const keywordEl = document.getElementById('keyword');
    const keyword = keywordEl ? keywordEl.value.trim() : '';
    if (!keyword) return;

    if (!ps) {
        alert('地図の準備がまだできていません。しばらくしてからもう一度お試しください。');
        return;
    }

    ps.keywordSearch(keyword, placesSearchCB);
}

function placesSearchCB(data, status, pagination) {
    if (status !== kakao.maps.services.Status.OK) {
        clearResults();
        return;
    }
    displayPlaces(data);
    displayPagination(pagination);
}

function displayPlaces(places) {
    const listEl = document.getElementById('placesList');
    const bounds = new kakao.maps.LatLngBounds();

    clearMarkers();
    if (listEl) listEl.innerHTML = '';

    places.forEach((place) => {
        const position = new kakao.maps.LatLng(place.y, place.x);
        bounds.extend(position);

        const marker = addMarker(position);
        const itemEl = makeListItem(place);

        itemEl.addEventListener('click', () => selectPlace(place, position));
        kakao.maps.event.addListener(marker, 'click', () => selectPlace(place, position));

        if (listEl) listEl.appendChild(itemEl);
    });

    if (map) map.setBounds(bounds);
}

function makeListItem(place) {
    const li = document.createElement('li');
    const addr = place.road_address_name || place.address_name || '';
    li.innerHTML = `
        <div style="font-weight:bold;">${escapeHtml(place.place_name)}</div>
        <div style="font-size:12px; color:#666; margin-top:4px;">${escapeHtml(addr)}</div>
    `;
    li.style.cursor = 'pointer';
    return li;
}

function addMarker(position) {
    const marker = new kakao.maps.Marker({position, map});
    markers.push(marker);
    return marker;
}

function selectPlace(place, position) {
    const lat = position.getLat();
    const lng = position.getLng();
    const addr = place.road_address_name || place.address_name || '';

    const placeNameEl = document.getElementById('placeName');
    const placeIdEl = document.getElementById('placeId');
    const latEl = document.getElementById('lat');
    const lngEl = document.getElementById('lng');
    const addressEl = document.getElementById('address');

    if (placeNameEl) placeNameEl.value = place.place_name || '';
    if (placeIdEl) placeIdEl.value = place.id || '';
    if (latEl) latEl.value = String(lat);
    if (lngEl) lngEl.value = String(lng);
    if (addressEl) addressEl.value = addr;

    addSchedulePlace({
        placeId: String(place.id || ''),
        placeName: String(place.place_name || ''),
        address: String(addr || ''),
        lat: Number(lat),
        lng: Number(lng)
    });
}

function addSchedulePlace(item) {
    if (!item.placeName) return;

    const key = item.placeId || `${item.placeName}|${item.lat}|${item.lng}`;
    const exists = schedulePlaces.some(p => (p.placeId || `${p.placeName}|${p.lat}|${p.lng}`) === key);
    if (exists) return;

    schedulePlaces.push(item);
    renderSchedulePlaces();
}

function removeSchedulePlace(index) {
    if (index < 0 || index >= schedulePlaces.length) return;
    schedulePlaces.splice(index, 1);
    renderSchedulePlaces();
}

function initRegionSelects() {
    const provinceEl = document.getElementById('province');
    const cityEl = document.getElementById('region');
    if (!provinceEl || !cityEl) return;

    const provinces = Object.keys(REGION_MAP);
    provinces.forEach((p) => {
        const opt = document.createElement('option');
        opt.value = p;
        opt.textContent = p;
        provinceEl.appendChild(opt);
    });

    const initialCity = cityEl.getAttribute('data-initial') || '';
    if (initialCity) {
        const found = findProvinceByCity(initialCity);
        if (found) {
            provinceEl.value = found;
            fillCities(found, initialCity);
            return;
        }
    }

    provinceEl.addEventListener('change', function () {
        fillCities(provinceEl.value, null);
    });
}

function fillCities(province, selectCity) {
    const cityEl = document.getElementById('region');
    if (!cityEl) return;

    cityEl.innerHTML = '';

    const placeholder = document.createElement('option');
    placeholder.value = '';
    placeholder.disabled = true;
    placeholder.selected = true;
    placeholder.textContent = '選択してください';
    cityEl.appendChild(placeholder);

    const cities = REGION_MAP[province] || [];
    cities.forEach((c) => {
        const opt = document.createElement('option');
        opt.value = c;
        opt.textContent = c;
        cityEl.appendChild(opt);
    });

    if (selectCity && cities.includes(selectCity)) {
        cityEl.value = selectCity;
    }
}

function findProvinceByCity(city) {
    const target = String(city).trim();
    if (!target) return null;
    for (const [prov, cities] of Object.entries(REGION_MAP)) {
        if (cities.includes(target)) return prov;
    }
    return null;
}

function initDateTimeRules() {
    const startEl = document.getElementById('startAt');
    const endEl = document.getElementById('endAt');
    const durEl = document.getElementById('durationMinutes');

    if (!startEl || !endEl || !durEl) return;

    const minStart = new Date(Date.now() + 24 * 60 * 60 * 1000);
    const minStartStr = toDatetimeLocalValue(minStart);
    startEl.min = minStartStr;

    if (!startEl.value || startEl.value.trim().length === 0) {
        startEl.value = minStartStr;
    }

    const startDate0 = parseDatetimeLocal(startEl.value);

    if (!endEl.value || endEl.value.trim().length === 0) {
        const fixEnd = new Date(startDate0.getTime() + 30 * 60 * 1000);
        endEl.value = toDatetimeLocalValue(fixEnd);
    }

    applyEndMinAndFixIfNeeded(true);
    updateDuration();

    startEl.addEventListener('change', function () {
        const selected = parseDatetimeLocal(startEl.value);
        const nowPlus24 = new Date(Date.now() + 24 * 60 * 60 * 1000);

        if (selected < nowPlus24) {
            alert('投稿作成時点から24時間以降の日付のみ選択できます。');
            startEl.value = toDatetimeLocalValue(nowPlus24);
        }

        applyEndMinAndFixIfNeeded(false);
        updateDuration();
    });

    endEl.addEventListener('change', function () {
        applyEndMinAndFixIfNeeded(false);
        updateDuration();
    });

    function applyEndMinAndFixIfNeeded(isInit) {
        const startDate = parseDatetimeLocal(startEl.value);
        const minEnd = new Date(startDate.getTime() + 30 * 60 * 1000);
        endEl.min = toDatetimeLocalValue(minEnd);

        const endDate = parseDatetimeLocal(endEl.value);

        if (endDate < startDate) {
            if (!isInit) {
                alert('終了日は開始日より前の日付を選択できません。');
            }
            endEl.value = toDatetimeLocalValue(minEnd);
            return;
        }

        if (endDate < minEnd) {
            if (!isInit) {
                alert('終了日時は開始日時から30分後以降のみ選択できます。');
            }
            endEl.value = toDatetimeLocalValue(minEnd);
        }
    }

    function updateDuration() {
        const startDate = parseDatetimeLocal(startEl.value);
        const endDate = parseDatetimeLocal(endEl.value);
        const diffMs = endDate.getTime() - startDate.getTime();
        const minutes = Math.floor(diffMs / 60000);
        durEl.value = (minutes > 0) ? String(minutes) : '';
    }
}

function validateOnSubmit() {
    const startEl = document.getElementById('startAt');
    const endEl = document.getElementById('endAt');
    const durEl = document.getElementById('durationMinutes');
    const provinceEl = document.getElementById('province');
    const cityEl = document.getElementById('region');

    const themeEl = document.getElementById('theme');
    const themeCustomEl = document.getElementById('themeCustom');

    if (themeEl && themeEl.value === 'OTHER') {
        const v = themeCustomEl ? String(themeCustomEl.value || '').trim() : '';
        if (!v) {
            alert('その他を選択した場合は、10文字未満でテーマを入力してください。');
            return false;
        }
        if (v.length >= 10) {
            alert('その他のテーマは10文字未満で入力してください。');
            return false;
        }
    }

    const nowPlus24 = new Date(Date.now() + 24 * 60 * 60 * 1000);
    const startDate = parseDatetimeLocal(startEl.value);
    if (startDate < nowPlus24) {
        alert('投稿作成時点から24時間以降の日付のみ選択できます。');
        return false;
    }

    const endDate = parseDatetimeLocal(endEl.value);

    if (endDate < startDate) {
        alert('終了日は開始日より前の日付を選択できません。');
        return false;
    }

    const minEnd = new Date(startDate.getTime() + 30 * 60 * 1000);
    if (endDate < minEnd) {
        alert('終了日時は開始日時から30分後以降のみ選択できます。');
        return false;
    }

    const duration = Number(durEl.value);
    if (!Number.isFinite(duration) || duration <= 0) {
        alert('終了日は開始日より前の日付を選択できません。');
        return false;
    }

    if (duration < 30) {
        alert('終了日時は開始日時から30分後以降のみ選択できます。');
        return false;
    }

    const checkedLang = document.querySelectorAll('input[name="languageCodes"]:checked');
    if (!checkedLang || checkedLang.length === 0) {
        alert('対応言語を1つ以上選択してください。');
        return false;
    }

    if (!provinceEl || !provinceEl.value) {
        alert('道を選択してください。');
        return false;
    }
    if (!cityEl || !cityEl.value) {
        alert('市を選択してください。');
        return false;
    }

    return true;
}

function parseDatetimeLocal(v) {
    return new Date(v);
}

function toDatetimeLocalValue(date) {
    const pad = (n) => String(n).padStart(2, '0');
    const yyyy = date.getFullYear();
    const mm = pad(date.getMonth() + 1);
    const dd = pad(date.getDate());
    const hh = pad(date.getHours());
    const mi = pad(date.getMinutes());
    return `${yyyy}-${mm}-${dd}T${hh}:${mi}`;
}

function loadSchedulePlacesFromHidden() {
    const hiddenRoot = document.getElementById('scheduleHiddenFieldsServer');
    if (!hiddenRoot) return;

    const placeNameInputs = hiddenRoot.querySelectorAll('input[name^="schedulePlaces["][name$=".placeName"]');
    if (!placeNameInputs || placeNameInputs.length === 0) return;

    schedulePlaces = [];

    for (let i = 0; i < placeNameInputs.length; i++) {
        const base = `schedulePlaces[${i}]`;

        const placeId = hiddenRoot.querySelector(`input[name="${base}.placeId"]`)?.value ?? '';
        const placeName = hiddenRoot.querySelector(`input[name="${base}.placeName"]`)?.value ?? '';
        const address = hiddenRoot.querySelector(`input[name="${base}.address"]`)?.value ?? '';
        const latStr = hiddenRoot.querySelector(`input[name="${base}.lat"]`)?.value ?? '';
        const lngStr = hiddenRoot.querySelector(`input[name="${base}.lng"]`)?.value ?? '';

        if (!placeName) continue;

        const lat = latStr !== '' ? Number(latStr) : null;
        const lng = lngStr !== '' ? Number(lngStr) : null;

        schedulePlaces.push({
            placeId,
            placeName,
            address,
            lat,
            lng
        });
    }
}

function renderSchedulePlaces() {
    const listEl = document.getElementById('scheduleList');
    const emptyEl = document.getElementById('scheduleEmptyText');
    const hiddenEl = document.getElementById('scheduleHiddenFields');

    if (!listEl || !hiddenEl) return;

    listEl.querySelectorAll('.tw-schedule-item').forEach(n => n.remove());
    hiddenEl.innerHTML = '';

    if (emptyEl) {
        emptyEl.style.display = schedulePlaces.length === 0 ? 'block' : 'none';
    }

    schedulePlaces.forEach((p, idx) => {
        const row = document.createElement('div');
        row.className = 'tw-schedule-item';
        row.style.display = 'flex';
        row.style.justifyContent = 'space-between';
        row.style.alignItems = 'center';
        row.style.padding = '10px 0';
        row.style.borderBottom = '1px solid rgba(15,23,42,0.08)';

        const left = document.createElement('div');
        left.innerHTML = `
            <div style="font-weight:900;">${escapeHtml(p.placeName)}</div>
            <div style="font-size:12px; color:#666; margin-top:4px;">${escapeHtml(p.address || '')}</div>
        `;

        const btn = document.createElement('button');
        btn.type = 'button';
        btn.textContent = '削除';
        btn.style.padding = '8px 10px';
        btn.style.borderRadius = '10px';
        btn.style.border = '1px solid rgba(15,23,42,0.12)';
        btn.style.background = '#fff';
        btn.style.cursor = 'pointer';
        btn.addEventListener('click', () => removeSchedulePlace(idx));

        row.appendChild(left);
        row.appendChild(btn);
        listEl.appendChild(row);

        hiddenEl.appendChild(makeHidden(`schedulePlaces[${idx}].placeId`, p.placeId || ''));
        hiddenEl.appendChild(makeHidden(`schedulePlaces[${idx}].placeName`, p.placeName || ''));
        hiddenEl.appendChild(makeHidden(`schedulePlaces[${idx}].address`, p.address || ''));
        hiddenEl.appendChild(makeHidden(`schedulePlaces[${idx}].lat`, p.lat != null ? String(p.lat) : ''));
        hiddenEl.appendChild(makeHidden(`schedulePlaces[${idx}].lng`, p.lng != null ? String(p.lng) : ''));
    });
}

function makeHidden(name, value) {
    const input = document.createElement('input');
    input.type = 'hidden';
    input.name = name;
    input.value = value;
    return input;
}

function clearMarkers() {
    markers.forEach(m => m.setMap(null));
    markers = [];
}

function clearResults() {
    const listEl = document.getElementById('placesList');
    const pagEl = document.getElementById('pagination');
    if (listEl) listEl.innerHTML = '';
    if (pagEl) pagEl.innerHTML = '';
    clearMarkers();
}

function displayPagination(pagination) {
    const pagEl = document.getElementById('pagination');
    if (!pagEl) return;

    pagEl.innerHTML = '';

    for (let i = 1; i <= pagination.last; i++) {
        const a = document.createElement('a');
        a.href = '#';
        a.textContent = String(i);
        a.style.marginRight = '8px';

        if (i === pagination.current) {
            a.style.fontWeight = '900';
        } else {
            a.addEventListener('click', (e) => {
                e.preventDefault();
                pagination.gotoPage(i);
            });
        }
        pagEl.appendChild(a);
    }
}

function escapeHtml(str) {
    return String(str)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}