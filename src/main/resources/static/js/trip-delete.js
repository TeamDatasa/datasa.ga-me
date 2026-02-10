(() => {
  const form = document.querySelector("#deleteTripForm");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const ok = confirm("정말 삭제하시겠습니까?");
    if (!ok) return;

    try {
      const res = await fetch(form.action, {
        method: "POST",
        credentials: "include",
        headers: {
          "X-Requested-With": "XMLHttpRequest",
        },
      });

      if (res.ok) {
        alert("삭제 되었습니다");
        window.location.href = "/trip/listAll";
        return;``
      }

      const text = await res.text().catch(() => "");
      alert(`삭제 실패 (${res.status})\n${text}`);
    } catch (err) {
      alert("삭제 요청 중 오류가 발생했습니다.");
    }
  });
})();
