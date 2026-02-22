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
      setDisabled("로그인 후 신청 가능");
      return;
    }

    if (Number(currentUserId) === Number(hostUserId)) {
      setDisabled("본인 여정");
      return;
    }

    const extraDisabled = String(applyBtn.dataset.applyExtraDisabled || "").toLowerCase() === "true";
    const extraReason = applyBtn.dataset.applyExtraReason || "";

    if (extraDisabled) {
      setDisabled(extraReason || "신청 불가");
      return;
    }

    const checkRes = await authFetch(`/api/applications/trips/${tripId}/me`, { method: "GET" });
    if (checkRes.ok) {
      const applied = await checkRes.json();
      if (applied === true) {
        setDisabled("신청한 여정");
        return;
      }
    }

    setEnabled("여정 신청하기");

    applyBtn.addEventListener(
        "click",
        async () => {
          if (applyBtn.disabled) return;

          const res = await authFetch(`/api/applications/trips/${tripId}`, { method: "POST" });
          const msg = await res.text();

          if (!res.ok) {
            alert(msg || "신청 처리에 실패했습니다.");
            return;
          }

          setDisabled("신청한 여정");
          alert("신청이 완료되었습니다.");
        },
        { once: true }
    );
  }

  initApplyButton().catch((e) => {
    console.error("[apply] init error", e);
    setDisabled("로그인 후 신청 가능");
  });
});