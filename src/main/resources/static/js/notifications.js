(function () {
  const wrap = document.querySelector('[data-notif-wrap]');
  if (!wrap) return;

  const bellBtn = wrap.querySelector('[data-notif-bell]');
  const badge = wrap.querySelector('[data-notif-badge]');
  const popover = wrap.querySelector('[data-notif-popover]') || document.querySelector('[data-notif-popover]');
  const list = popover ? popover.querySelector('[data-notif-list]') : null;
  const empty = popover ? popover.querySelector('[data-notif-empty]') : null;
  const markReadBtn = popover ? popover.querySelector('[data-notif-markread]') : null;

  let pollingTimer = null;

  function setBadge(count) {
    const n = Number(count || 0);
    if (!badge) return;

    if (n > 0) {
      badge.textContent = n > 99 ? '99+' : String(n);
      badge.style.display = 'inline-block';
    } else {
      badge.style.display = 'none';
    }
  }

  function formatTime(iso) {
    if (!iso) return '';
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) return String(iso);
    return d.toLocaleString();
  }

  function buildTargetUrl(n) {
    // refId를 tripId로 쓰는 기준
    if (!n || !n.refId) return null;

    // ✅ 프로젝트 라우팅에 맞게 수정 가능
    // 기존 코드: `/trip/${n.refId}` 는 실제로는 `/trip/detail/{id}`일 가능성이 큼
    if (n.type === 'COMMENT' || n.type === 'LIKE') {
      return `/trip/detail/${n.refId}`;
    }
    // 신청/승인 같은 타입도 trip 상세로 보내고 싶으면:
    if (n.type === 'APPLY' || n.type === 'APPROVED' || n.type === 'REJECTED') {
      return `/trip/detail/${n.refId}`;
    }
    return null;
  }

  async function apiJson(url, options = {}) {
    const res = await fetch(url, {
      credentials: 'include',
      headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
      ...options,
    });

    if (res.status === 401 || res.status === 403) {
      // 로그인 상태가 아니면 알림 UI 숨김
      wrap.style.display = 'none';
      throw new Error('AUTH_BLOCKED');
    }

    if (!res.ok) {
      throw new Error('HTTP_' + res.status);
    }

    if (res.status === 204) return null;
    return res.json();
  }

  async function refreshUnread() {
    const data = await apiJson('/api/notifications/unread-count');
    setBadge(data ? data.unreadCount : 0);
  }

  function render(items) {
    if (!list || !empty) return;

    list.innerHTML = '';

    if (!items || items.length === 0) {
      empty.style.display = 'block';
      return;
    }
    empty.style.display = 'none';

    for (const n of items) {
      const li = document.createElement('li');
      li.className = 'notif-item';
      if (!n.isRead) li.classList.add('is-unread');

      // ✅ (질문하신) dataset: "이 li가 어떤 알림(id)인지" 저장하는 용도
      // 꼭 필요하진 않지만, 디버깅/추가기능에 좋음
      li.dataset.notifId = n.notificationId;

      const url = buildTargetUrl(n);

      // ✅ 전체 클릭 이동 영역
      const container = document.createElement(url ? 'a' : 'div');
      if (url) {
        container.href = url;
        container.className = 'notif-item__link';
      } else {
        container.className = 'notif-item__link'; // 스타일 통일
      }

      const title = document.createElement('div');
      title.className = 'notif-item__title';
      title.textContent = n.title || '(알림)';

      const body = document.createElement('div');
      body.className = 'notif-item__body';
      body.textContent = n.body || '';

      const meta = document.createElement('div');
      meta.className = 'notif-item__meta';
      meta.textContent = `${n.type || ''} · ${formatTime(n.createdAt)}`;

      container.appendChild(title);
      container.appendChild(body);
      container.appendChild(meta);

      // ✅ X 삭제 버튼
      const delBtn = document.createElement('button');
      delBtn.type = 'button';
      delBtn.className = 'notif-item__delete';
      delBtn.setAttribute('aria-label', '알림 삭제');
      delBtn.textContent = '×';

      delBtn.addEventListener('click', async (e) => {
        // a 링크 이동 방지 + 버블링 방지
        e.preventDefault();
        e.stopPropagation();

        const id = n.notificationId;
        if (!id) return;

        try {
          await apiJson(`/api/notifications/${encodeURIComponent(id)}`, { method: 'DELETE' });

          // 화면에서 즉시 제거
          li.remove();

          // 남은 항목 0이면 empty 표시
          if (list.children.length === 0) {
            empty.style.display = 'block';
          }

          // unread 배지 갱신
          await refreshUnread();
        } catch (_) {
          // 실패 시 조용히 종료 (원하면 alert/toast 가능)
        }
      });

      li.appendChild(delBtn);
      li.appendChild(container);
      list.appendChild(li);
    }
  }

  async function refreshList(limit = 10) {
    const items = await apiJson(`/api/notifications?limit=${encodeURIComponent(limit)}`);
    render(items);
  }

  function openPopover() {
    if (!popover) return;
    popover.classList.add('open');
  }

  function closePopover() {
    if (!popover) return;
    popover.classList.remove('open');
  }

  async function togglePopover() {
    if (!popover) return;

    const isOpen = popover.classList.contains('open');
    if (isOpen) {
      closePopover();
      return;
    }

    openPopover();
    await refreshList(10);
  }

  function startPolling() {
    stopPolling();
    pollingTimer = setInterval(() => {
      refreshUnread().catch(() => {});
    }, 15000);
  }

  function stopPolling() {
    if (pollingTimer) {
      clearInterval(pollingTimer);
      pollingTimer = null;
    }
  }

  bellBtn && bellBtn.addEventListener('click', async (e) => {
    e.stopPropagation();
    try {
      await togglePopover();
    } catch (_) {}
  });

  document.addEventListener('click', (e) => {
    if (!popover) return;
    if (!popover.classList.contains('open')) return;

    const isInside = popover.contains(e.target) || wrap.contains(e.target);
    if (!isInside) closePopover();
  });

  markReadBtn && markReadBtn.addEventListener('click', async () => {
    try {
      await apiJson('/api/notifications/read-all', { method: 'POST' });
      await refreshUnread();
      await refreshList(10);
    } catch (_) {}
  });

  // 초기 로딩
  refreshUnread().catch(() => {});
  startPolling();
})();
