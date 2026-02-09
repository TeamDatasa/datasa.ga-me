function authHeaders() {
  return { "Content-Type": "application/json" };
}

// =============================
// 표시용 변환 함수
// =============================
function countryCodeToKorean(code) {
  const map = { KR: "대한민국", JP: "일본", CN: "중국", US: "미국" };
  return map[code] || code || "-";
}

function genderToKorean(gender) {
  const map = { FEMALE: "여성", MALE: "남성", OTHER: "기타" };
  return map[gender] || "-";
}

function formatBirthDate(iso) {
  // 서버에서 "YYYY-MM-DD"로 내려온다고 가정
  if (!iso) return "-";
  return iso; // 필요하면 "YYYY. MM. DD."로 포맷도 가능
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
  if (roleBadge) roleBadge.textContent = roleToLabel(role);
}

function setHint(msg = "") {
  const hint = document.getElementById("saveHint");
  if (hint) hint.textContent = msg;
}

// =============================
// Readonly lock helpers
// (HTML에서 readonly로 두는 걸 추천하지만, JS에서도 안전하게 잠금)
// =============================
function lockBasicFields() {
  ["name", "birthDate", "gender", "country", "region"].forEach((id) => {
    const el = document.getElementById(id);
    if (!el) return;

    // 기본정보는 무조건 표시용
    el.readOnly = true;
    el.setAttribute("readonly", "readonly");
    el.classList.add("readonly-input");
  });
}

// =============================
// Host Card Profile (Public Toggle)
// =============================
async function loadHostCardPublicStatus() {
  const res = await authFetch("/api/mypage/host/card", { method: "GET" });
  if (!res || !res.ok) return;

  const data = await res.json();
  console.log("[host/card GET]", data);
  const toggle = document.getElementById("hostCardPublicToggle");
  if (toggle) toggle.checked = !!data.isPublic;
}

async function updateHostCardPublic(isPublic) {
  const res = await authFetch("/api/mypage/host/card", {
    method: "PATCH",
    headers: authHeaders(),
    body: JSON.stringify({ isPublic }),
  });

  if (!res || !res.ok) {
    alert("카드프로필 공개 설정 변경에 실패했습니다.");
    await loadHostCardPublicStatus();
  }
}

// =============================
// Load profile
// =============================
async function loadProfile() {
  const res = await authFetch("/api/mypage/profile", { method: "GET" });
  if (!res) return;

  if (!res.ok) {
    alert("프로필 정보를 불러오지 못했습니다.");
    return;
  }

  const data = await res.json();

  // ===== Top card =====
  const displayNameEl = document.getElementById("displayName");
  const displayMetaEl = document.getElementById("displayMeta");
  const avatarEl = document.getElementById("avatar");

  if (displayNameEl) displayNameEl.textContent = data.name ?? "-";

  const meta = `${data.region ?? "-"} · ${data.age ?? "-"}세 · ${countryCodeToKorean(data.countryCode)}`;
  if (displayMetaEl) displayMetaEl.textContent = meta;

  if (avatarEl) avatarEl.textContent = data.name ? data.name[0] : "?";

  // ===== Form values (기본정보는 표시용) =====
  const nameEl = document.getElementById("name");
  const birthEl = document.getElementById("birthDate");
  const genderEl = document.getElementById("gender");
  const countryEl = document.getElementById("country");
  const regionEl = document.getElementById("region");

  if (nameEl) nameEl.value = data.name ?? "";
  if (birthEl) birthEl.value = formatBirthDate(data.birthDate);
  if (genderEl) genderEl.value = genderToKorean(data.gender);
  if (countryEl) countryEl.value = countryCodeToKorean(data.countryCode);
  if (regionEl) regionEl.value = data.region ?? "";

  // ===== Editable fields =====
  const mbtiEl = document.getElementById("mbti");
  const smokingEl = document.getElementById("smoking");
  const drinkingEl = document.getElementById("drinking");
  const bioEl = document.getElementById("bio");

  if (mbtiEl) mbtiEl.value = data.mbti ?? "";

  if (smokingEl) {
    smokingEl.value = data.smoking === true ? "true" : data.smoking === false ? "false" : "";
  }

  if (drinkingEl) {
    drinkingEl.value = data.drinking === true ? "true" : data.drinking === false ? "false" : "";
  }

  if (bioEl) bioEl.value = data.bio ?? "";

  // ===== Role UI =====
  setRoleUI(data.role ?? "USER");

  // ✅ 기본 정보 잠금
  lockBasicFields();

  // ✅ HOST면 카드프로필 공개 상태도 로드
  if ((data.role ?? "USER") === "HOST") {
    await loadHostCardPublicStatus();
  }
}

// =============================
// Save profile (ONLY editable fields)
// =============================
function bindProfileSave() {
  const form = document.getElementById("profileForm");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    setHint("");

    const mbtiRaw = document.getElementById("mbti")?.value ?? "";
    const mbtiTrim = mbtiRaw.trim();
    const mbti = mbtiTrim === "" ? null : mbtiTrim;

    if (mbti && !/^[EI][SN][TF][JP]$/i.test(mbti)) {
          setHint("MBTI를 제대로 입력해 주세요.");
          document.getElementById("mbti")?.focus();
          return;
        }

    const smokingRaw = document.getElementById("smoking")?.value ?? "";
    const smoking = smokingRaw === "" ? null : smokingRaw === "true";

    const drinkingRaw = document.getElementById("drinking")?.value ?? "";
    const drinking = drinkingRaw === "" ? null : drinkingRaw === "true";

    const bio = document.getElementById("bio")?.value?.trim() || null;

    // ✅ 기본정보는 절대 보내지 않음
    const payload = {
        mbti: mbti ? mbti.toUpperCase() : null,
        smoking,
        drinking,
        bio
        };

    const res = await authFetch("/api/mypage/profile", {
      method: "PUT",
      headers: authHeaders(),
      body: JSON.stringify(payload),
    });
    if (!res) return;

    if (res.ok) {
      setHint("저장되었습니다.");
     return;
    } else {
          const text = await res.text().catch(() => "");
          setHint(text || "저장에 실패했습니다. 입력값을 확인해 주세요.");
        }
    });
}

// =============================
// Role change (IMMEDIATE save)
// =============================
async function setRole(role) {
  setRoleUI(role);

  const res = await authFetch("/api/mypage/role", {
    method: "PATCH",
    headers: authHeaders(),
    body: JSON.stringify({ role }),
  });
  if (!res) return;

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    alert(text || "역할 변경에 실패했습니다. 잠시 후 다시 시도해 주세요.");
    await loadProfile();
    return;
  }

  setHint("역할이 변경되었습니다.");
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
      await fetch("/api/auth/logout", {
        method: "POST",
        credentials: "same-origin",
      });
    } catch (e) {
      console.warn("[logout] request failed:", e);
    }

    window.location.href = "/";
  });

  document.getElementById("deactivateBtn")?.addEventListener("click", async () => {
    const ok = confirm("정말로 회원 탈퇴하시겠습니까?\n탈퇴 후에는 로그인이 불가능합니다.");
    if (!ok) return;

    const res = await authFetch("/api/mypage", { method: "DELETE" });
    if (!res) return;

    if (!res.ok) {
      const text = await res.text().catch(() => "");
      alert(text || "회원 탈퇴에 실패했습니다. 잠시 후 다시 시도해 주세요.");
      return;
    }

    try {
      await fetch("/api/auth/logout", {
        method: "POST",
        credentials: "same-origin",
      });
    } catch (e) {
      console.warn("[logout after deactivate] request failed:", e);
    }

    alert("회원 탈퇴가 완료되었습니다.");
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

  setRoleUI(document.getElementById("currentRole")?.value || "USER");

  const toggle = document.getElementById("hostCardPublicToggle");
  if (toggle) {
    toggle.addEventListener("change", () => {
      updateHostCardPublic(toggle.checked);
    });
  }

  await loadProfile();
});

function roleToLabel(role) {
  if (role === "HOST") return "Guide";
  if (role === "USER") return "Traveler";
  return "-";
}
