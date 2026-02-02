(function () {
  const wrap = document.querySelector('[data-notif-wrap]');
  if (!wrap) return;

  const bellBtn = wrap.querySelector('[data-notif-bell]');
  const badge = wrap.querySelector('[data-notif-badge]');
  const popover = wrap.querySelector('[data-notif-popover]') || document.querySelector('[data-notif-popover]');
  const list = popover ? popover.querySelector('[data-notif-list]') : null;
  const empty = popover ? popover.querySelector('[data-notif-empty]') : null;
  const markReadBtn = popover ? popover.querySelector('[data-notif-markread]') : null;
  const testBtn = wrap.querySelector('[data-notif-test]');

  let pollingTimer = null; //

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
    // : 댓글 알림은 게시글(trip) 상세로 이동
    // refId를 tripId로 쓰는 기준
    if (!n || !n.refId) return null;

    if (n.type === 'COMMENT' || n.type === 'LIKE') {
      return `/trip/${n.refId}`;
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
      // : 로그인 상태가 아니면 UI를 숨김
      wrap.style.display = 'none';
      throw new Error('AUTH_BLOCKED');
    }

    if (!res.ok) {
      throw new Error('HTTP_' + res.status);
    }

    // 204 대응
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
      if (!n.isRead) li.classList.add('is-unread'); //

      const url = buildTargetUrl(n); //

      const container = document.createElement(url ? 'a' : 'div');
      if (url) {
        container.href = url;
        container.className = 'notif-item__link';
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
    await refreshList(10); // : 열 때 목록 갱신
  }

  function startPolling() {
    // : 일정 주기로 unread 갱신 (실시간 푸시 전 임시 방식)
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
    } catch (_) {
      // : 네트워크/인증 실패 시 조용히 종료
    }
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
    } catch (_) {
      //
    }
  });

  // : 테스트 버튼은 엔드포인트가 없을 수 있으므로 404는 무시
  testBtn && testBtn.addEventListener('click', async () => {
    try {
      await apiJson('/api/notifications/test-comment', { method: 'POST' });
      await refreshUnread();
      if (popover && popover.classList.contains('open')) {
        await refreshList(10);
      } else {
        openPopover();
        await refreshList(10);
      }
    } catch (e) {
      // : 현재 프로젝트 NotificationController에 test-comment가 없으면 404가 정상입니다.
    }
  });

  // 초기 로딩
  refreshUnread().catch(() => {});
  startPolling(); //
})();
