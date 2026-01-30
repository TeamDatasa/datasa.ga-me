(function () {
  const wrap = document.querySelector('[data-notif-wrap]');
  if (!wrap) return;

  const bellBtn = wrap.querySelector('[data-notif-bell]');
  const badge = wrap.querySelector('[data-notif-badge]');
  const popover = document.querySelector('[data-notif-popover]');
  const list = popover?.querySelector('[data-notif-list]');
  const empty = popover?.querySelector('[data-notif-empty]');
  const markReadBtn = popover?.querySelector('[data-notif-markread]');
  const testBtn = wrap.querySelector('[data-notif-test]');

  async function api(url, options = {}) {
    const res = await fetch(url, {
      credentials: 'include',
      headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
      ...options,
    });

     console.log('[notif] fetch', url, res.status);


    if (res.status === 401 || res.status === 403) {
      throw new Error('AUTH_BLOCKED_' + res.status);
    }
    return res;
  }

  function formatTime(iso) {
    if (!iso) return '';
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) return iso;
    return d.toLocaleString();
  }

  function setBadge(count) {
    const n = Number(count || 0);
    if (n > 0) {
      badge.textContent = n > 99 ? '99+' : String(n);
      badge.style.display = 'inline-block';
    } else {
      badge.style.display = 'none';
    }
  }

  async function refreshUnread() {
    const res = await api('/api/notifications/unread-count');
    const data = await res.json();
    setBadge(data.unreadCount);
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

      const title = document.createElement('div');
      title.className = 'notif-item__title';
      title.textContent = n.title || '(알림)';

      const body = document.createElement('div');
      body.className = 'notif-item__body';
      body.textContent = n.body || '';

      const meta = document.createElement('div');
      meta.className = 'notif-item__meta';
      meta.textContent = `${n.type || ''} · ${formatTime(n.createdAt)}`;

      li.appendChild(title);
      li.appendChild(body);
      li.appendChild(meta);

      list.appendChild(li);
    }
  }

  async function refreshList() {
    const res = await api('/api/notifications?limit=10');
    const items = await res.json();
    render(items);
  }

  async function openPopover() {
    if (!popover) return;
    popover.classList.add('open');
    await refreshList();
  }

  function closePopover() {
    if (!popover) return;
    popover.classList.remove('open');
  }

  bellBtn?.addEventListener('click', async (e) => {
    e.stopPropagation();
    if (!popover) return;

    if (popover.classList.contains('open')) {
      closePopover();
      return;
    }
    await openPopover();
  });

  document.addEventListener('click', (e) => {
    if (!popover) return;
    if (!popover.classList.contains('open')) return;

    const isInside = popover.contains(e.target) || wrap.contains(e.target);
    if (!isInside) closePopover();
  });

  markReadBtn?.addEventListener('click', async () => {
    await api('/api/notifications/read-all', { method: 'POST' });
    await refreshUnread();
    await refreshList();
  });

testBtn?.addEventListener('click', async () => {
  await api('/api/notifications/test-comment', { method: 'POST' });

  await refreshUnread();

  if (!popover?.classList.contains('open')) {
    await openPopover();
  } else {
    await refreshList();
  }
});


  refreshUnread()
    .then(() => console.log('[notif] unread loaded'))
    .catch((e) => console.log('[notif] unread failed', e));

})();
