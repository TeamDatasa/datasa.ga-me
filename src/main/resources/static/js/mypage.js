// =============================
// Auth header helper
// =============================
function authHeaders() {
  const token = localStorage.getItem("accessToken");
  return {
    "Content-Type": "application/json",
    "Authorization": `Bearer ${token}`
  };
}

// =============================
// Country mapping
// =============================
function countryNameToCode(name) {
  if (!name) return null;
  const n = name.trim().toLowerCase();
  const map = {
    "korea": "KR",
    "south korea": "KR",
    "republic of korea": "KR",
    "japan": "JP",
    "china": "CN",
    "usa": "US",
    "united states": "US"
  };
  return map[n] || null;
}

function countryCodeToName(code) {
  const map = { KR: "Korea", JP: "Japan", CN: "China", US: "USA" };
  return map[code] || code || "";
}

// =============================
// UI helpers
// =============================
function setRoleUI(role) {
  const currentRoleEl = document.getElementById("currentRole");
  if (currentRoleEl) currentRoleEl.value = role;

  document.querySelectorAll(".role-pill").forEach((btn) => {
    btn.classList.toggle("is-active", btn.dataset.role === role);
  });

  const hostPanel = document.getElementById("hostPanel");
  const userPanel = document.getElementById("userPanel");
  if (hostPanel) hostPanel.style.display = role === "HOST" ? "block" : "none";
  if (userPanel) userPanel.style.display = role === "USER" ? "block" : "none";

  const roleBadge = document.getElementById("roleBadge");
  if (roleBadge) roleBadge.textContent = role ?? "-";
}

function setHint(msg = "") {
  const hint = document.getElementById("saveHint");
  if (hint) hint.textContent = msg;
}

function setDetailsLink(role) {
  const link = document.getElementById("detailsLink");
  if (!link) return;

  // ✅ URL 분리 방식 추천
  link.href = (role === "HOST")
    ? "/mypage/details/host"
    : "/mypage/details/user";
}


// =============================
// Load profile
// =============================
async function loadProfile() {
  const token = localStorage.getItem("accessToken");

  if (!token) {
    alert("Login is required.");
    window.location.href = "/auth/login";
    return;
  }

  const res = await fetch("/api/mypage/profile", { headers: authHeaders() });

  if (res.status === 401 || res.status === 403) {
    alert("Session expired. Please log in again.");
    localStorage.removeItem("accessToken");
    window.location.href = "/auth/login";
    return;
  }

  if (!res.ok) {
    alert("Failed to load profile.");
    return;
  }

  const data = await res.json();

  // ===== Top card =====
  document.getElementById("displayName").textContent = data.name ?? "-";
  document.getElementById("displayMeta").textContent =
    `${data.region ?? "-"} · ${data.age ?? "-"} yrs · ${countryCodeToName(data.countryCode) ?? "-"}`;
  document.getElementById("avatar").textContent = data.name ? data.name[0] : "?";

  // ===== Form values =====
  document.getElementById("name").value = data.name ?? "";
  document.getElementById("birthDate").value = data.birthDate ?? "";
  document.getElementById("gender").value = data.gender ?? "";

  const countryEl = document.getElementById("country");
  if (countryEl) countryEl.value = countryCodeToName(data.countryCode);

  document.getElementById("region").value = data.region ?? "";
  document.getElementById("mbti").value = data.mbti ?? "";

  document.getElementById("smoking").value =
    data.smoking === true ? "true" : data.smoking === false ? "false" : "";

  document.getElementById("drinking").value =
    data.drinking === true ? "true" : data.drinking === false ? "false" : "";

  document.getElementById("bio").value = data.bio ?? "";

  // ===== Role UI (pills/panels/badge) =====
  setRoleUI(data.role ?? "USER");
}

// =============================
// Save profile (ONLY on Save button)
// =============================
function bindProfileSave() {
  const form = document.getElementById("profileForm");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
      name: document.getElementById("name").value.trim(),
      birthDate: document.getElementById("birthDate").value,
      gender: document.getElementById("gender").value,

      // country name -> code
      countryCode: countryNameToCode(document.getElementById("country")?.value),

      region: document.getElementById("region").value.trim(),
      mbti: document.getElementById("mbti").value.trim() || null,

      smoking: document.getElementById("smoking").value === "" ? null :
        document.getElementById("smoking").value === "true",

      drinking: document.getElementById("drinking").value === "" ? null :
        document.getElementById("drinking").value === "true",

      bio: document.getElementById("bio").value.trim() || null
    };

    const res = await fetch("/api/mypage/profile", {
      method: "PUT",
      headers: authHeaders(),
      body: JSON.stringify(payload)
    });

    if (res.ok) {
      setHint("Profile saved successfully.");
      await loadProfile();
    } else {
      const text = await res.text().catch(() => "");
      setHint(text || "Failed to save profile. Please check your input.");
    }
  });
}

// =============================
// Role change (IMMEDIATE save)
// =============================
async function setRole(role) {
  // 1️⃣ UI 먼저 반영 (즉시 체감)
  setRoleUI(role);

  const res = await fetch("/api/mypage/role", {
    method: "PATCH",
    headers: authHeaders(),
    body: JSON.stringify({ role })
  });

  if (res.status === 401 || res.status === 403) {
    alert("Session expired. Please log in again.");
    localStorage.clear();
    window.location.href = "/auth/login";
    return;
  }

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    alert(text || "Failed to update role. Please try again.");
    await loadProfile(); // 서버 값으로 되돌리기
    return;
  }

  // 2️⃣ ✅ 서버에서 내려준 최신 AuthResponse 받기
  const data = await res.json();

  // 3️⃣ ✅ 토큰 + 사용자 정보 갱신
  localStorage.setItem("accessToken", data.accessToken);
  localStorage.setItem("userId", data.userId);
  localStorage.setItem("email", data.email);
  localStorage.setItem("role", data.role);


  setHint("Role updated.");

  setDetailsLink(role);
  setRoleUI(data.role);


}



// (HTML onclick="setRole('HOST')" 에서 접근 가능하게 전역 노출)
window.setRole = setRole;

// =============================
// Logout / Delete
// =============================
function bindAccountActions() {
  document.getElementById("logoutBtn")?.addEventListener("click", () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userId");
    localStorage.removeItem("email");
    localStorage.removeItem("role");
    window.location.href = "/auth/login";
  });

  document.getElementById("deactivateBtn")?.addEventListener("click", async () => {
    const ok = confirm("Are you sure you want to delete your account?");
    if (!ok) return;

    const res = await fetch("/api/mypage", {
      method: "DELETE",
      headers: authHeaders()
    });

    if (!res.ok) {
      alert("Failed to delete account.");
      return;
    }

    alert("Your account has been deleted.");
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userId");
    localStorage.removeItem("email");
    localStorage.removeItem("role");
    window.location.href = "/";
  });

  document.getElementById("resetPwBtn")?.addEventListener("click", () => {
    const email = localStorage.getItem("email") || "";
    window.location.href = `/auth/forgot-password?email=${encodeURIComponent(email)}`;
  });
}

// =============================
// Init
// =============================
document.addEventListener("DOMContentLoaded", async () => {
  bindProfileSave();
  bindAccountActions();

  // 초기 UI는 loadProfile이 서버 값으로 덮어씀
  setRoleUI(document.getElementById("currentRole")?.value || "USER");

  await loadProfile();
});
