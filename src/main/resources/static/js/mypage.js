function authHeaders() {
  return { "Content-Type": "application/json" };
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
  const hostCardPanel = document.getElementById("hostCardPanel");

  if (hostPanel) hostPanel.style.display = role === "HOST" ? "block" : "none";
  if (userPanel) userPanel.style.display = role === "USER" ? "block" : "none";
  if (hostCardPanel) hostCardPanel.style.display = role === "HOST" ? "block" : "none";

  const roleBadge = document.getElementById("roleBadge");
  if (roleBadge) roleBadge.textContent = role ?? "-";
}

function setHint(msg = "") {
  const hint = document.getElementById("saveHint");
  if (hint) hint.textContent = msg;
}

// =============================
// Host Card Profile (Public Toggle)
// =============================
async function loadHostCardPublicStatus() {
  const res = await authFetch("/api/mypage/host/card", { method: "GET" });
  if (!res || !res.ok) return;

  const data = await res.json();
  const toggle = document.getElementById("hostCardPublicToggle");
  if (toggle) toggle.checked = !!data.isPublic;
}

async function updateHostCardPublic(isPublic) {
  const res = await authFetch("/api/mypage/host/card", {
    method: "PATCH",
    headers: authHeaders(),
    body: JSON.stringify({ isPublic })
  });

  if (!res || !res.ok) {
    alert("카드프로필 공개 설정 변경에 실패했습니다.");
    await loadHostCardPublicStatus(); // 서버 값으로 복구
  }
}

// =============================
// Load profile
// =============================
async function loadProfile() {
  const res = await authFetch("/api/mypage/profile", { method: "GET" });
  if (!res) return;

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

  // ===== Role UI =====
  setRoleUI(data.role ?? "USER");

  // ✅ HOST면 카드프로필 공개 상태도 로드
  if ((data.role ?? "USER") === "HOST") {
    await loadHostCardPublicStatus();
  }
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

      countryCode: countryNameToCode(document.getElementById("country")?.value),

      region: document.getElementById("region").value.trim(),
      mbti: document.getElementById("mbti").value.trim() || null,

      smoking: document.getElementById("smoking").value === "" ? null :
        document.getElementById("smoking").value === "true",

      drinking: document.getElementById("drinking").value === "" ? null :
        document.getElementById("drinking").value === "true",

      bio: document.getElementById("bio").value.trim() || null
    };

    const res = await authFetch("/api/mypage/profile", {
      method: "PUT",
      headers: authHeaders(),
      body: JSON.stringify(payload)
    });
    if (!res) return;

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
  // UI 먼저 반영
  setRoleUI(role);

  const res = await authFetch("/api/mypage/role", {
    method: "PATCH",
    headers: authHeaders(),
    body: JSON.stringify({ role })
  });
  if (!res) return;

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    alert(text || "Failed to update role. Please try again.");
    await loadProfile(); // 서버 값으로 되돌리기
    return;
  }

  setHint("Role updated.");
  await loadProfile();
}

window.setRole = setRole;

// =============================
// Logout / Delete
// =============================
function bindAccountActions() {
  document.getElementById("logoutBtn")?.addEventListener("click", async () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userId");
    localStorage.removeItem("email");
    localStorage.removeItem("role");
    sessionStorage.removeItem("justLoggedIn");

    try {
      const res = await fetch("/api/auth/logout", {
        method: "POST",
        credentials: "same-origin"
      });
      console.log("[logout] status:", res.status);
    } catch (e) {
      console.warn("[logout] request failed:", e);
    }

    window.location.href = "/";
  });

  document.getElementById("deactivateBtn")?.addEventListener("click", async () => {
    const ok = confirm("Are you sure you want to delete your account?");
    if (!ok) return;

    const res = await authFetch("/api/mypage", { method: "DELETE" });
    if (!res) return;

    if (!res.ok) {
      alert("Failed to delete account.");
      return;
    }

    alert("Your account has been deleted.");
    window.location.href = "/";
  });

  document.getElementById("resetPwBtn")?.addEventListener("click", () => {
    window.location.href = "/auth/forgot-password";
  });
}

// =============================
// Init
// =============================
document.addEventListener("DOMContentLoaded", async () => {
  bindProfileSave();
  bindAccountActions();

  // 초기 UI는 서버값(loadProfile)로 확정됨
  setRoleUI(document.getElementById("currentRole")?.value || "USER");

  // ✅ 토글 이벤트
  const toggle = document.getElementById("hostCardPublicToggle");
  if (toggle) {
    toggle.addEventListener("change", () => {
      updateHostCardPublic(toggle.checked);
    });
  }

  await loadProfile();
});
