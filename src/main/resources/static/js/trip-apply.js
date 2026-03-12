document.addEventListener("DOMContentLoaded", () => {
  const applyBtn = document.getElementById("applyBtn");
  if (!applyBtn) return;

  function setDisabled(text) {
    applyBtn.disabled = true;
    applyBtn.classList.add("is-disabled");
    applyBtn.textContent = text;
  }

  function setEnabled(text) {
    applyBtn.disabled = false;
    applyBtn.classList.remove("is-disabled");
    applyBtn.textContent = text;
  }

  async function initApplyButton() {
    console.log("[apply] loaded", { tripId, hostUserId, currentUserId });

    if (currentUserId == null) {
      setDisabled("ログイン後に申請できます");
      return;
    }

    if (Number(currentUserId) === Number(hostUserId)) {
      setDisabled("自分のツアー");
      return;
    }

    const extraDisabled = String(applyBtn.dataset.applyExtraDisabled || "").toLowerCase() === "true";
    const extraReason = applyBtn.dataset.applyExtraReason || "";

    if (extraDisabled) {
      setDisabled(extraReason || "申請不可");
      return;
    }

    const checkRes = await authFetch(`/api/applications/trips/${tripId}/me`, { method: "GET" });
    if (checkRes.ok) {
      const applied = await checkRes.json();
      if (applied === true) {
        setDisabled("申請済みのツアー");
        return;
      }
    }

    setEnabled("ツアーに申請する");

    applyBtn.addEventListener(
        "click",
        async () => {
          if (applyBtn.disabled) return;

          const res = await authFetch(`/api/applications/trips/${tripId}`, { method: "POST" });
          const msg = await res.text();

          if (!res.ok) {
            alert(msg || "申請処理に失敗しました。");
            return;
          }

          setDisabled("申請済みのツアー");
          alert("申請が完了しました。");
        },
        { once: true }
    );
  }

  initApplyButton().catch((e) => {
    console.error("[apply] init error", e);
    setDisabled("ログイン後に申請できます");
  });
});