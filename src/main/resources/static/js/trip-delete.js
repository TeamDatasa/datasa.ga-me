(() => {
  const form = document.querySelector("#deleteTripForm");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const ok = confirm("本当に削除しますか？");
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
        alert("削除しました。");
        window.location.href = "/trip/listAll";
        return;``
      }

      const text = await res.text().catch(() => "");
      alert(`削除失敗 (${res.status})\n${text}`);
    } catch (err) {
      alert("削除リクエスト中にエラーが発生しました。");
    }
  });
})();
