document.addEventListener('DOMContentLoaded', function () {
  const locations =
      (Array.isArray(window.tripLocations) ? window.tripLocations : null) ||
      (typeof tripLocations !== 'undefined' && Array.isArray(tripLocations) ? tripLocations : null);

  if (!locations || locations.length === 0) return;

  const mapEl = document.getElementById('tripMap');
  if (!mapEl) return;

  if (!(window.kakao && kakao.maps && typeof kakao.maps.load === 'function')) return;

  kakao.maps.load(function () {
    renderTripMap(mapEl, locations);
  });
});

function renderTripMap(mapEl, locations) {
  const points = locations
      .filter(p => p && isFiniteNumber(p.lat) && isFiniteNumber(p.lng))
      .slice()
      .sort((a, b) => (toInt(a.orderNo) - toInt(b.orderNo)));

  if (points.length === 0) return;

  const first = points[0];
  const center = new kakao.maps.LatLng(Number(first.lat), Number(first.lng));

  const map = new kakao.maps.Map(mapEl, {
    center: center,
    level: 5
  });

  map.setDraggable(true);
  map.setZoomable(true);

  const bounds = new kakao.maps.LatLngBounds();
  const linePath = [];

  points.forEach((p) => {
    const latLng = new kakao.maps.LatLng(Number(p.lat), Number(p.lng));
    bounds.extend(latLng);
    linePath.push(latLng);

    new kakao.maps.Marker({
      map: map,
      position: latLng
    });

    const no = toInt(p.orderNo);
    const overlay = new kakao.maps.CustomOverlay({
      position: latLng,
      yAnchor: 1,
      content: buildBadgeHtml(no > 0 ? no : ''),
      clickable: false
    });
    overlay.setMap(map);
  });

  if (linePath.length >= 2) {
    const polyline = new kakao.maps.Polyline({
      map: map,
      path: linePath,
      strokeWeight: 4,
      strokeColor: '#1675F2',
      strokeOpacity: 0.8,
      strokeStyle: 'solid'
    });
    polyline.setMap(map);
  }

  map.setBounds(bounds);

  if (points.length === 1) {
    map.setLevel(4);
    map.setCenter(center);
  }
}

function buildBadgeHtml(text) {
  const safe = escapeHtml(String(text));
  return `
        <div class="dt-map-badge">
            <div class="dt-map-badge__text">${safe}</div>
        </div>
    `.trim();
}

function toInt(v) {
  const n = Number(v);
  if (!Number.isFinite(n)) return 0;
  return Math.trunc(n);
}

function isFiniteNumber(v) {
  const n = Number(v);
  return Number.isFinite(n);
}

function escapeHtml(s) {
  return String(s).replace(/[&<>"']/g, (m) => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    "\"": "&quot;",
    "'": "&#39;"
  }[m]));
}