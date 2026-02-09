// 파일: src/main/resources/static/js/notifications.js
(function () {
  const wrap = document.querySelector('[data-notif-wrap]');
  if (!wrap) return;

  const bellBtn = wrap.querySelector('[data-notif-bell]');
  const badge = wrap.querySelector('[data-notif-badge]');
  const popover = wrap.querySelector('[data-notif-popover]') || document.querySelector('[data-notif-popover]');
  const list = popover ? popover.querySelector('[data-notif-list]') : null;
  const empty = popover ? popover.querySelector('[data-notif-empty]') : null;
  const markReadBtn = popover ? popover.querySelector('[data-notif-markread]') : null;

  // `추가: 토스트 영역
  const toastArea = wrap.querySelector('[data-notif-toast-area]');

  let pollingTimer = null;

  // `추가: 이전 unread 값/마지막 토스트로 띄운 알림ID 기억
  let lastUnreadCount = null;
  let lastToastNotificationId = null;

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
    if (!n || !n.refId) return null;
    if (n.type === 'COMMENT' || n.type === 'LIKE') return `/trip/detail/${n.refId}`;
    if (n.type === 'APPLY' || n.type === 'APPROVED' || n.type === 'REJECTED') return `/trip/detail/${n.refId}`;
    return null;
  }

  async function apiJson(url, options = {}) {
    const res = await fetch(url, {
      credentials: 'include',
      headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
      ...options,
    });

    if (res.status === 401 || res.status === 403) {
      wrap.style.display = 'none';
      throw new Error('AUTH_BLOCKED');
    }

    if (!res.ok) throw new Error('HTTP_' + res.status);
    if (res.status === 204) return null;
    return res.json();
  }

  // 추가: 토스트 표시
  function showToast(notification) {
    if (!toastArea || !notification) return;

    // 같은 알림을 중복 토스트 방지
    if (notification.notificationId && notification.notificationId === lastToastNotificationId) return;
    lastToastNotificationId = notification.notificationId || lastToastNotificationId;

    const url = buildTargetUrl(notification);

    const toast = document.createElement('div');
    toast.className = 'notif-toast';

    const title = document.createElement('div');
    title.className = 'notif-toast__title';
    title.textContent = notification.title || '알림';

    const body = document.createElement('div');
    body.className = 'notif-toast__body';
    body.textContent = notification.body || '';

    const meta = document.createElement('div');
    meta.className = 'notif-toast__meta';
    meta.textContent = formatTime(notification.createdAt);

    toast.appendChild(title);
    toast.appendChild(body);
    toast.appendChild(meta);

    toast.addEventListener('click', () => {
      if (url) window.location.href = url;
    });

    toastArea.appendChild(toast);

    // 4초 후 자동 제거
    setTimeout(() => {
      toast.remove();
    }, 4000);
  }

  // `변경: unread 갱신 시 증가 감지 → 최신 알림 1개 토스트
  async function refreshUnread() {
    const data = await apiJson('/api/notifications/unread-count');
    const current = data ? Number(data.unreadCount || 0) : 0;

    // 최초 1회는 비교하지 않고 기준만 세팅
    if (lastUnreadCount === null) {
      lastUnreadCount = current;
      setBadge(current);
      return;
    }

    // 증가했으면 최신 알림 1개 가져와 토스트
    if (current > lastUnreadCount) {
      try {
        const items = await apiJson('/api/notifications?limit=1');
        if (items && items.length > 0) {
          showToast(items[0]);
        }
      } catch (_) {}
    }

    lastUnreadCount = current;
    setBadge(current);
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

      li.dataset.notifId = n.notificationId;

      const url = buildTargetUrl(n);

      const container = document.createElement(url ? 'a' : 'div');
      if (url) {
        container.href = url;
        container.className = 'notif-item__link';
      } else {
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

      const delBtn = document.createElement('button');
      delBtn.type = 'button';
      delBtn.className = 'notif-item__delete';
      delBtn.setAttribute('aria-label', '알림 삭제');
      delBtn.textContent = '×';

      delBtn.addEventListener('click', async (e) => {
        e.preventDefault();
        e.stopPropagation();

        const id = n.notificationId;
        if (!id) return;

        try {
          await apiJson(`/api/notifications/${encodeURIComponent(id)}`, { method: 'DELETE' });
          li.remove();
          if (list.children.length === 0) empty.style.display = 'block';
          await refreshUnread();
        } catch (_) {}
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

  refreshUnread().catch(() => {});
  startPolling();
})();
