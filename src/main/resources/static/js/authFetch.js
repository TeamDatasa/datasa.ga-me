async function authFetch(url, options = {}) {
  const token = localStorage.getItem("accessToken");

  const headers = {
    ...(options.headers || {}),
    "Content-Type": options.headers?.["Content-Type"] || "application/json",
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  // 최종 요청
  const response = await fetch(url, {
    ...options,
    headers,
  });

  // 401이면 토큰 만료/미로그인 → 로그인 페이지로
  if (response.status === 401) {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userId");
    localStorage.removeItem("email");
    localStorage.removeItem("role");
    alert("Your session has expired. Please sign in again.");
    window.location.href = "/login";
    return;
  }

  return response;
}
