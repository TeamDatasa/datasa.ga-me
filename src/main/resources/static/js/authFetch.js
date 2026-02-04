// authFetch.js (완성본)
// - 쿠키 기반 JWT 전제
// - 401이면: (1) 토큰/저장정보 제거 (2) 로그아웃 API 시도 (3) 홈으로 리다이렉트
export async function authFetch(url, options = {}) {
  const headers = { ...(options.headers || {}) };

  // JSON body일 때만 Content-Type 자동 세팅
  const isFormData = options.body instanceof FormData;
  if (!headers["Content-Type"] && options.body && !isFormData) {
    headers["Content-Type"] = "application/json";
  }

  const res = await fetch(url, {
    ...options,
    headers,
    credentials: "include", // ⭐ 쿠키 JWT 핵심
  });

  if (res.status === 401) {
    // 저장 정보 정리(혼용 대비)
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userId");
    localStorage.removeItem("email");
    localStorage.removeItem("role");

    try {
      await fetch("/api/auth/logout", {
        method: "POST",
        credentials: "include",
      });
    } catch (_) {}

    window.location.href = "/";
    throw new Error("UNAUTHORIZED");
  }

  return res;
}
