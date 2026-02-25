async function requestCancel(btn) {
    const applicationId = btn.dataset.applicationId;
    if (!applicationId) return;

    const ok = confirm("참가 취소를 신청하시겠습니까?");
    if (!ok) return;

    const res = await fetch(`/api/cancel-requests/applications/${applicationId}`, {
        method: "POST",
        credentials: "same-origin"
    });

    if (!res.ok) {
        const msg = await res.text().catch(() => "");
        alert(msg || "취소 신청에 실패했습니다.");
        return;
    }

    location.reload();
}
