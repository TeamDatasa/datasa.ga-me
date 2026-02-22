(() => {
  const $ = (sel) => document.querySelector(sel);

  const form = $("#commentForm");
  const contentEl = $("#commentContent");
  const listEl = $("#commentList");
  const countEl = $("#commentCount");

  if (!form || !contentEl || !listEl) return;

  async function fetchComments() {
    const res = await fetch(`/api/trips/${tripId}/comments`, { credentials: "include" });
    if (!res.ok) {
      console.error("댓글 목록 조회 실패", res.status);
      return [];
    }
    return res.json();
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

  function escapeHtml(s) {
    return String(s ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
  }

  function render(comments) {
    const visible = Array.isArray(comments) ? comments : [];
    if (countEl) countEl.textContent = String(visible.length);

    listEl.innerHTML = visible
        .map((c) => {
          const mine = currentUserId && c.userId && Number(currentUserId) === Number(c.userId);
          return `
          <li class="comment-item" data-comment-id="${c.commentId}">
            <div class="comment-meta">
              <strong class="comment-author">${escapeHtml(c.userName)}</strong>
              <span class="comment-time">${formatTime(c.createdAt)}</span>
            </div>
            <div class="comment-content" data-content>${escapeHtml(c.content)}</div>
            <div class="comment-actions">
              <button type="button" class="btn-like" data-like>♥ ${c.likeCount}</button>
              ${mine ? `<button type="button" class="btn-edit" data-edit>수정</button>
                        <button type="button" class="btn-del" data-del>삭제</button>` : ``}
            </div>
          </li>
        `;
        })
        .join("");
  }


  async function refresh() {
    const comments = await fetchComments();
    render(comments);
  }

  // 댓글 작성
  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const content = contentEl.value.trim();
    if (!content) return;

    const res = await authFetch(`/api/trips/${tripId}/comments`, {
      method: "POST",
      body: JSON.stringify({ parentCommentId: null, content }),
    });

    if (!res.ok) {
      const msg = await res.text().catch(() => "");
      alert(`댓글 작성 실패 (${res.status})\n${msg}`);
      return;
    }

    contentEl.value = "";
    await refresh();
  });

  // 좋아요/수정/삭제 (이벤트 위임)
  listEl.addEventListener("click", async (e) => {
    const li = e.target.closest("li[data-comment-id]");
    if (!li) return;
    const commentId = li.getAttribute("data-comment-id");

    // 좋아요
    if (e.target.matches("[data-like]")) {
      const res = await authFetch(`/api/comments/${commentId}/like`, { method: "POST" });
      if (res.ok) {
        const likeCount = await res.json();
        e.target.textContent = `♥ ${likeCount}`;
      }
      return;
    }

    // 삭제
    if (e.target.matches("[data-del]")) {
      if (!confirm("댓글을 삭제하시겠습니까?")) return;

      const res = await authFetch(`/api/comments/${commentId}`, { method: "DELETE" });
      if (!res.ok) {
        const msg = await res.text().catch(() => "");
        alert(`삭제 실패 (${res.status})\n${msg}`);
        return;
      }
      await refresh();
      return;
    }

    // 수정
    if (e.target.matches("[data-edit]")) {
      const contentDiv = li.querySelector("[data-content]");
      const old = contentDiv ? contentDiv.textContent : "";
      const next = prompt("수정할 내용을 입력하세요.", old);
      if (next == null) return;

      const content = next.trim();
      if (!content) return;

      const res = await authFetch(`/api/comments/${commentId}`, {
        method: "PUT",
        body: JSON.stringify({ content }),
      });

      if (!res.ok) {
        const msg = await res.text().catch(() => "");
        alert(`수정 실패 (${res.status})\n${msg}`);
        return;
      }

      await refresh();
    }
  });

  // 초기 로드
  refresh();
})();
