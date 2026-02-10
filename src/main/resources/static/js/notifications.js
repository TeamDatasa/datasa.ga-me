(() => {
  const badgeEl = document.querySelector("[data-notif-badge]");
  const dropdownEl = document.querySelector("[data-notif-dropdown]");
  const listEl = document.querySelector("[data-notif-list]");
  const emptyEl = document.querySelector("[data-notif-empty]");

  function setBadge(count) {
    if (!badgeEl) return;
    if (!count || Number(count) <= 0) {
      badgeEl.style.display = "none";
      badgeEl.textContent = "";
      return;
    }
    badgeEl.style.display = "inline-flex";
    badgeEl.textContent = String(count);
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

    const count = await res.json();
    setBadge(count);
  }

  async function loadLatest() {
    if (typeof authFetch !== "function" || !listEl) return;

    const res = await authFetch("/api/notifications/latest", {
      method: "GET",
      redirectOn401: false,
    });

    if (res.status === 401) {
      listEl.innerHTML = "";
      if (emptyEl) emptyEl.style.display = "block";
      return;
    }

    if (!res.ok) return;

    const items = await res.json();

    if (!Array.isArray(items) || items.length === 0) {
      listEl.innerHTML = "";
      if (emptyEl) emptyEl.style.display = "block";
      return;
    }

    if (emptyEl) emptyEl.style.display = "none";

    listEl.innerHTML = items
        .map((n) => {
          const title = n.title ?? "";
          const body = n.body ?? "";
          const refId = n.refId ?? "";
          return `
          <li class="notif-item">
            <a class="notif-link" href="/trip/detail/${refId}">
              <div class="notif-title">${escapeHtml(title)}</div>
              <div class="notif-body">${escapeHtml(body)}</div>
            </a>
          </li>
        `;
        })
        .join("");
  }

  function escapeHtml(s) {
    return String(s ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
  }

  if (dropdownEl) {
    dropdownEl.addEventListener("click", async () => {
      await loadLatest();
    });
  }

  loadUnreadCount();
  setInterval(loadUnreadCount, 15000);
})();
