(function () {
  function qsAll(sel, root = document) {
    return Array.from(root.querySelectorAll(sel));
  }

  async function apiJson(url, options = {}) {
    const res = await fetch(url, {
      credentials: 'include',
      headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
      ...options,
    });

    if (res.status === 401 || res.status === 403) throw new Error('AUTH');
    if (!res.ok) throw new Error('HTTP_' + res.status);

    // 204 대응
    if (res.status === 204) return null;
    return res.json();
  }

  function setBtnState(btn, liked, count) {
    btn.dataset.liked = liked ? 'true' : 'false';
    btn.dataset.count = String(count ?? 0);

    const icon = btn.querySelector('[data-like-icon]');
    const cnt = btn.querySelector('[data-like-count]');

    if (icon) icon.textContent = liked ? '♥' : '♡';
    if (cnt) cnt.textContent = String(count ?? 0);

    btn.classList.toggle('is-liked', !!liked);
  }

  async function handleClick(btn) {
    if (btn.dataset.loading === 'true') return;

    const tripId = btn.dataset.tripId;
    if (!tripId) return;

    // 현재 UI 상태
    const beforeLiked = btn.dataset.liked === 'true';
    const beforeCount = Number(btn.dataset.count || btn.querySelector('[data-like-count]')?.textContent || 0);

    // ✅ 즉시 반영(낙관적 업데이트)
    const nextLiked = !beforeLiked;
    const nextCount = Math.max(0, beforeCount + (nextLiked ? 1 : -1));
    setBtnState(btn, nextLiked, nextCount);

    // 요청
    btn.dataset.loading = 'true';
    btn.classList.add('is-loading');

    try {
      // ⚠️ 여기 URL은 너희 좋아요 API에 맞춰야 함
      // 예: POST /api/trip/{tripId}/like  -> { liked: boolean, count: number }
      const data = await apiJson(`/api/trip/${encodeURIComponent(tripId)}/like`, { method: 'POST' });

      // 서버 최종값으로 싱크
      if (data && typeof data.liked === 'boolean' && typeof data.count === 'number') {
        setBtnState(btn, data.liked, data.count);
      }
    } catch (e) {
      // 실패 시 원복
      setBtnState(btn, beforeLiked, beforeCount);
    } finally {
      btn.dataset.loading = 'false';
      btn.classList.remove('is-loading');
    }
  }

  // 페이지 내 모든 좋아요 버튼 바인딩
  qsAll('[data-like-btn]').forEach((btn) => {
    // 최초 DOM 상태 정리(Thymeleaf 값이 문자열로 오므로 보정)
    const liked = btn.dataset.liked === 'true' || btn.dataset.liked === 'True';
    const count = Number(btn.dataset.count || 0);
    setBtnState(btn, liked, count);

    btn.addEventListener('click', (e) => {
      e.preventDefault();
      e.stopPropagation();
      handleClick(btn);
    });
  });
})();
