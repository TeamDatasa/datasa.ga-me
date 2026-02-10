(() => {
  const wrapEl = document.querySelector("[data-notif-wrap]");
  const bellBtn = document.querySelector("[data-notif-bell]");
  const popoverEl = document.querySelector("[data-notif-popover]");
  const badgeEl = document.querySelector("[data-notif-badge]");
  const listEl = document.querySelector("[data-notif-list]");
  const emptyEl = document.querySelector("[data-notif-empty]");
  const markReadBtn = document.querySelector("[data-notif-markread]");

  if (!wrapEl || !bellBtn || !popoverEl || !badgeEl) return;

  function setBadge(count) {
    const n = Number(count || 0);
    if (n <= 0) {
      badgeEl.style.display = "none";
      badgeEl.textContent = "";
      return;
    }
    badgeEl.style.display = "inline-flex";
    badgeEl.textContent = String(n);
  }

  function escapeHtml(s) {
    return String(s ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
  }

  function formatTime(iso) {
    if (!iso) return "";
    const d = new Date(iso);
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, "0");
    const dd = String(d.getDate()).padStart(2, "0");
    const hh = String(d.getHours()).padStart(2, "0");
    const mi = String(d.getMinutes()).padStart(2, "0");
    return `${yyyy}-${mm}-${dd} ${hh}:${mi}`;
  }

  function notifLink(n) {
    const refId = n.refId;
    if (!refId) return "#";
    return `/trip/detail/${refId}`;
  }

  function renderList(items) {
    if (!listEl) return;

    if (!Array.isArray(items) || items.length === 0) {
      listEl.innerHTML = "";
      if (emptyEl) emptyEl.style.display = "block";
      return;
    }

    if (emptyEl) emptyEl.style.display = "none";

    listEl.innerHTML = items
        .map((n) => {
          const href = notifLink(n);
          const title = escapeHtml(n.title ?? "");
          const body = escapeHtml(n.body ?? "");
          const time = formatTime(n.createdAt);

          const readClass = n.isRead ? " is-read" : "";
          return `
          <li class="notif-item${readClass}">
            <a class="notif-link" href="${href}">
              <div class="notif-title">${title}</div>
              <div class="notif-body">${body}</div>
              <div class="notif-time">${escapeHtml(time)}</div>
            </a>
          </li>
        `;
        })
        .join("");
  }

  async function loadUnreadCount() {
    if (typeof authFetch !== "function") return;

    const res = await authFetch("/api/notifications/unread-count", {
      method: "GET",
      redirectOn401: false,
    });

    if (res.status === 401) {
      setBadge(0);
      return;
    }

    if (!res.ok) {
      setBadge(0);
      return;
    }

    const data = await res.json();
    setBadge(data?.unreadCount ?? 0);
  }

  async function loadRecent() {
    if (typeof authFetch !== "function" || !listEl) return;

    const res = await authFetch("/api/notifications?limit=10", {
      method: "GET",
      redirectOn401: false,
    });

    if (res.status === 401) {
      renderList([]);
      setBadge(0);
      return;
    }

    if (!res.ok) return;

    const items = await res.json();
    renderList(items);
  }

  async function markAllRead() {
    if (typeof authFetch !== "function") return;

    const res = await authFetch("/api/notifications/read-all", {
      method: "POST",
      redirectOn401: false,
    });

    if (!res.ok) return;

    await loadUnreadCount();
    await loadRecent();
  }

  function isOpen() {
    return popoverEl.classList.contains("open");
  }

  function openPopover() {
    popoverEl.classList.add("open");
  }

  function closePopover() {
    popoverEl.classList.remove("open");
  }


  bellBtn.addEventListener("click", async (e) => {
    e.preventDefault();
    e.stopPropagation();

    if (isOpen()) {
      closePopover();
      return;
    }

    openPopover();
    await loadRecent();
  });

  if (markReadBtn) {
    markReadBtn.addEventListener("click", async (e) => {
      e.preventDefault();
      e.stopPropagation();
      await markAllRead();
    });
  }

  document.addEventListener("click", (e) => {
    if (!isOpen()) return;
    if (wrapEl.contains(e.target)) return;
    closePopover();
  });

  loadUnreadCount();
  setInterval(loadUnreadCount, 15000);
})();
