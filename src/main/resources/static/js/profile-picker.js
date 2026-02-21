(() => {
  const PRESET_PROFILES = [
    "/images/profile/profile1.png",
    "/images/profile/profile2.png",
    "/images/profile/profile3.png",
    "/images/profile/profile4.png",
    "/images/profile/profile5.png",
    "/images/profile/profile6.png",
  ];

  const IDS = {
    openBtn: "openProfilePickerBtn",

    modal: "avatarModal",
    backdrop: "avatarModalBackdrop",
    grid: "avatarGrid",
    hint: "avatarHint",

    closeBtn: "closeAvatarBtn",
    cancelBtn: "cancelAvatarBtn",
    saveBtn: "saveAvatarBtn",

    avatar: "avatar",
    displayName: "displayName",

    previewImg: "avatarPreviewImg",
    previewText: "avatarPreviewText",
  };

  let selectedUrl = null;
  let currentUrl = null;

  function $(id) {
    return document.getElementById(id);
  }

  function openModal(modal) {
    modal.classList.add("is-open");
    modal.setAttribute("aria-hidden", "false");
  }

  function closeModal(modal, hintEl) {
    modal.classList.remove("is-open");
    modal.setAttribute("aria-hidden", "true");
    if (hintEl) hintEl.textContent = "";
  }

  function renderAvatar(url) {
    const avatarEl = $(IDS.avatar);
    if (!avatarEl) return;

    if (url) {
      avatarEl.textContent = "";
      avatarEl.style.backgroundImage = `url('${url}')`;
      avatarEl.style.backgroundSize = "cover";
      avatarEl.style.backgroundPosition = "center";
      avatarEl.style.borderRadius = "999px";
      return;
    }

    avatarEl.style.backgroundImage = "";
    const name = $(IDS.displayName)?.textContent?.trim() || "";
    avatarEl.textContent = name ? name[0] : "?";
  }

  function renderPreview(url, isCurrent) {
    const imgEl = $(IDS.previewImg);
    const textEl = $(IDS.previewText);
    if (!imgEl || !textEl) return;

    const label = isCurrent ? "현재 프로필" : "선택한 이미지";

    if (url) {
      imgEl.style.display = "block";
      imgEl.src = url;
      textEl.textContent = label;
      return;
    }

    imgEl.style.display = "none";
    imgEl.removeAttribute("src");
    textEl.textContent = "현재 프로필: 기본(이니셜)";
  }

  async function fetchProfile() {
    const res = await authFetch("/api/mypage/profile", { method: "GET" });
    if (!res || !res.ok) return null;
    return await res.json();
  }

  async function savePresetProfile(url) {
    const headers =
        typeof authHeaders === "function"
            ? authHeaders()
            : { "Content-Type": "application/json" };

    const res = await authFetch("/api/mypage/profile-image/preset", {
      method: "PUT",
      headers,
      body: JSON.stringify({ profileImageUrl: url }),
    });

    if (!res) throw new Error("no response");
    if (!res.ok) {
      const text = await res.text().catch(() => "");
      throw new Error(text || "save failed");
    }
    return await res.json();
  }

  function renderGrid(gridEl) {
    gridEl.innerHTML = "";

    PRESET_PROFILES.forEach((url) => {
      const btn = document.createElement("button");
      btn.type = "button";
      btn.className =
          "avatar-option" + (url === selectedUrl ? " is-selected" : "");

      const img = document.createElement("img");
      img.src = url;
      img.alt = "profile";
      btn.appendChild(img);

      btn.addEventListener("click", () => {
        selectedUrl = url;
        renderPreview(selectedUrl, selectedUrl === currentUrl);

        gridEl
            .querySelectorAll(".avatar-option")
            .forEach((el) => el.classList.remove("is-selected"));
        btn.classList.add("is-selected");
      });

      gridEl.appendChild(btn);
    });
  }

  async function bind() {
    const openBtn = $(IDS.openBtn);
    const modal = $(IDS.modal);
    const grid = $(IDS.grid);
    const hint = $(IDS.hint);
    const closeBtn = $(IDS.closeBtn);
    const cancelBtn = $(IDS.cancelBtn);
    const saveBtn = $(IDS.saveBtn);
    const backdrop = $(IDS.backdrop);

    if (!openBtn || !modal || !grid || !hint || !saveBtn) return;

    const close = () => closeModal(modal, hint);

    openBtn.addEventListener("click", async () => {
      hint.textContent = "";

      const profile = await fetchProfile();
      currentUrl = profile?.profileImageUrl || null;
      selectedUrl = currentUrl;

      renderGrid(grid);
      renderPreview(selectedUrl, true);
      openModal(modal);
    });

    closeBtn?.addEventListener("click", close);
    cancelBtn?.addEventListener("click", close);
    backdrop?.addEventListener("click", close);

    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape" && modal.classList.contains("is-open")) close();
    });

    saveBtn.addEventListener("click", async () => {
      if (!selectedUrl) {
        hint.textContent = "이미지를 선택해 주세요.";
        return;
      }

      saveBtn.disabled = true;
      hint.textContent = "저장 중입니다...";

      try {
        const updated = await savePresetProfile(selectedUrl);
        currentUrl = updated?.profileImageUrl || null;

        renderAvatar(currentUrl);
        renderPreview(currentUrl, true);

        if (typeof window.loadProfile === "function") {
          await window.loadProfile();
        }

        hint.textContent = "저장되었습니다.";
        setTimeout(close, 200);
      } catch (err) {
        console.error(err);
        hint.textContent = "저장에 실패했습니다. 잠시 후 다시 시도해 주세요.";
      } finally {
        saveBtn.disabled = false;
      }
    });
  }

  document.addEventListener("DOMContentLoaded", bind);
})();