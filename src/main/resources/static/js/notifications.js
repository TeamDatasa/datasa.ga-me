(() => {
  const wrapEl = document.querySelector("[data-notif-wrap]");
  const bellBtn = document.querySelector("[data-notif-bell]");
  const popoverEl = document.querySelector("[data-notif-popover]");
  const badgeEl = document.querySelector("[data-notif-badge]");
  const listEl = document.querySelector("[data-notif-list]");
  const emptyEl = document.querySelector("[data-notif-empty]");
  const markReadBtn = document.querySelector("[data-notif-markread]");

  if (!wrapEl || !bellBtn || !popoverEl || !badgeEl || !listEl) return;

  let unreadCount = 0;

  function setBadge(count) {
    const n = Math.max(0, Number(count || 0));
    unreadCount = n;

    if (n <= 0) {
      badgeEl.style.display = "none";
      badgeEl.textContent = "";
      return;
    }
    badgeEl.style.display = "inline-flex";
    badgeEl.textContent = String(n);
  }

  function decBadgeIfPossible() {
    if (unreadCount > 0) setBadge(unreadCount - 1);
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
          const time = escapeHtml(formatTime(n.createdAt));
          const unreadClass = n.isRead ? "" : " is-unread";

          return `
          <li class="notif-item${unreadClass}" data-notif-id="${n.notificationId}" data-is-read="${n.isRead}">
            <button type="button" class="notif-item__delete" data-notif-delete aria-label="알림 삭제">×</button>
            <a class="notif-item__link" data-notif-link href="${href}">
              <div class="notif-item__title">${title}</div>
              <div class="notif-item__body">${body}</div>
              <div class="notif-item__meta">${time}</div>
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
    if (typeof authFetch !== "function") return;

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

  async function markOneRead(notificationId) {
    if (typeof authFetch !== "function") return false;

    const res = await authFetch(`/api/notifications/${notificationId}/read`, {
      method: "POST",
      redirectOn401: false,
    });
    return res.ok;
  }

  async function deleteOne(notificationId) {
    if (typeof authFetch !== "function") return false;

    const res = await authFetch(`/api/notifications/${notificationId}`, {
      method: "DELETE",
      redirectOn401: false,
    });
    return res.ok;
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

  // 링크 클릭: 단건 읽음 처리 + 뱃지 감소
  // X 클릭: 단건 삭제 + (미읽음이었다면) 뱃지 감소
  listEl.addEventListener("click", async (e) => {
    const delBtn = e.target.closest("[data-notif-delete]");
    if (delBtn) {
      e.preventDefault();
      e.stopPropagation();

      const itemEl = delBtn.closest(".notif-item");
      const notificationId = itemEl?.getAttribute("data-notif-id");
      if (!notificationId) return;

      const wasRead = itemEl.getAttribute("data-is-read") === "true";
      const ok = await deleteOne(notificationId);
      if (!ok) return;

      itemEl.remove();
      if (!wasRead) decBadgeIfPossible();
      if (listEl.children.length === 0 && emptyEl) emptyEl.style.display = "block";
      return;
    }

    const link = e.target.closest("[data-notif-link]");
    if (!link) return;

    const itemEl = link.closest(".notif-item");
    const notificationId = itemEl?.getAttribute("data-notif-id");
    if (!notificationId) return;

    const alreadyRead = itemEl.getAttribute("data-is-read") === "true";
    if (alreadyRead) return;

    const ok = await markOneRead(notificationId);
    if (ok) {
      itemEl.classList.remove("is-unread");
      itemEl.setAttribute("data-is-read", "true");
      decBadgeIfPossible();
    }
  });

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
