async function authFetch(url, options = {}) {
  const headers = {
    ...(options.headers || {}),
    "Content-Type": options.headers?.["Content-Type"] || "application/json",
  };

  const response = await fetch(url, {
    ...options,
    headers,
    credentials: "same-origin"  // ✅ 쿠키 access_token 자동 전송
  });

  if (response.status === 401) {
    alert("Session expired. Please sign in again.");
    window.location.href = "/login";
    return;
  }

  return response;
}
