document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("signupForm");
  if (!form) return;

  const emailInput = document.getElementById("email");
  const sendCodeBtn = document.getElementById("sendCodeBtn");
  const verifyCodeBtn = document.getElementById("verifyCodeBtn");
  const codeInput = document.getElementById("verificationCode");
  const statusEl = document.getElementById("emailVerifyStatus");
  const signupBtn = document.getElementById("signupBtn");

  let isEmailVerified = false;

  const setStatus = (text) => {
    if (statusEl) statusEl.textContent = text;
  };

  const setVerifiedState = (verified) => {
    isEmailVerified = verified;

    if (signupBtn) signupBtn.disabled = !verified;

    if (verified) {
      setStatus("✅ Email verified.");
      if (sendCodeBtn) sendCodeBtn.disabled = true;
      if (verifyCodeBtn) verifyCodeBtn.disabled = true;
      if (emailInput) emailInput.readOnly = true;
      if (codeInput) codeInput.readOnly = true;
    }
  };

  if (emailInput) {
    emailInput.addEventListener("input", () => {
      isEmailVerified = false;
      if (signupBtn) signupBtn.disabled = true;
      if (sendCodeBtn) sendCodeBtn.disabled = false;
      if (verifyCodeBtn) verifyCodeBtn.disabled = false;
      if (emailInput) emailInput.readOnly = false;
      if (codeInput) codeInput.readOnly = false;
      setStatus("");
    });
  }

  // Send verification code
  if (sendCodeBtn) {
    sendCodeBtn.addEventListener("click", async () => {
      const email = emailInput?.value?.trim();
      if (!email) {
        alert("Please enter your email address.");
        return;
      }

      try {
        const res = await fetch("/api/auth/email/send-code", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email }),
        });

        if (!res.ok) {
          const text = await res.text().catch(() => "");
          throw new Error(text || "Failed to send verification code.");
        }

        setStatus("Verification code sent. Please check your email.");
        alert("Verification code sent.");
      } catch (err) {
        alert(err?.message || "Failed to send verification code.");
      }
    });
  }

  // Verify code
  if (verifyCodeBtn) {
    verifyCodeBtn.addEventListener("click", async () => {
      const email = emailInput?.value?.trim();
      const code = codeInput?.value?.trim();

      if (!email) {
        alert("Please enter your email address.");
        return;
      }
      if (!code) {
        alert("Please enter the 6-digit verification code.");
        return;
      }
      if (!/^\d{6}$/.test(code)) {
        alert("Verification code must be 6 digits.");
        return;
      }

      try {
        const res = await fetch("/api/auth/email/verify-code", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email, code }),
        });

        if (!res.ok) {
          const text = await res.text().catch(() => "");
          throw new Error(text || "Invalid or expired verification code.");
        }

        setVerifiedState(true);
        alert("Email verification successful.");
      } catch (err) {
        alert(err?.message || "Invalid or expired verification code.");
      }
    });
  }

  // Signup submit
  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = emailInput?.value?.trim();
    const password = document.getElementById("password")?.value ?? "";
    const passwordConfirm = document.getElementById("passwordConfirm")?.value ?? "";
    const name = document.getElementById("name")?.value?.trim();
    const roleElement = document.querySelector('input[name="role"]:checked');
    const role = roleElement ? roleElement.value : null;

    if (!email) return alert("Please enter your email address.");
    if (!password) return alert("Please enter your password.");
    if (!passwordConfirm) return alert("Please confirm your password.");
    if (password !== passwordConfirm) return alert("Passwords do not match.");
    if (!name) return alert("Please enter your name.");
    if (!role) return alert("Please select a user role.");

    if (!isEmailVerified) {
      alert("Please verify your email before signing up.");
      return;
    }

    const payload = { email, password, name, role };

    try {
      const response = await fetch("/api/auth/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const contentType = response.headers.get("content-type") || "";
        let errorMessage = "Sign up failed.";

        if (contentType.includes("application/json")) {
          const errorData = await response.json().catch(() => null);
          errorMessage = errorData?.message || errorData?.error || errorMessage;
        } else {
          const text = await response.text().catch(() => "");
          if (text) errorMessage = text;
        }

        throw new Error(errorMessage);
      }

      alert("Account created successfully!");
      window.location.href = "/auth/login";
    } catch (error) {
      alert(error?.message || "Sign up failed.");
    }
  });
});
