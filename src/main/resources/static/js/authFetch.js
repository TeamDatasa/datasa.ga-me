window.authFetch = async function authFetch(url, options = {}) {
  const headers = { ...(options.headers || {}) };

  const isFormData = options.body instanceof FormData;
  if (!headers["Content-Type"] && options.body && !isFormData) {
    headers["Content-Type"] = "application/json";
  }

  const res = await fetch(url, {
    ...options,
    headers,
    credentials: "include",
  });

  const redirectOn401 = options.redirectOn401 !== false;

  if (res.status === 401 && redirectOn401) {
    try {
      await fetch("/api/auth/logout", { method: "POST", credentials: "include" });
    } catch (_) {}
    window.location.href = "/";
    throw new Error("UNAUTHORIZED");
  }

  return res;
};
