# Unresolved Bugs and Open Risks

Items here are UNVERIFIED risks or confirmed open issues. They are not necessarily bugs —
some are risks that have not been investigated. Each entry notes what is known and what
would be needed to close it.

---

## RISK-001: Private Key File Rotation Status Unknown

**Type:** POSSIBLE DEFECT / Security Risk
**File:** `lightsail private key.txt` (untracked, gitignored — located in Portfolio project root)
**Severity:** MEDIUM — depends on rotation status

**What is known:** The file exists on disk as an untracked file. It is listed in .gitignore
and has never been committed to version control. The GitHub Actions deploy workflow uses a
separate secret (`LIGHTSAIL_SSH_KEY`) stored in GitHub repository secrets for actual deployments.

**What is unknown:** Whether the key in `lightsail private key.txt` has been rotated since
it was placed on disk. If the file represents the same key as the one registered on the
Lightsail instance and it were ever exposed, it would allow SSH access to the production server.

**Risk status:** UNVERIFIED — rotation not documented anywhere in the project

**To close this risk:**
1. Verify whether the key in the file is the same key registered on the Lightsail instance
2. If yes: rotate the Lightsail key pair and update the GitHub secret
3. Delete the file from disk after rotation is confirmed
4. Document the rotation date

---

## RISK-002: Production Deployment Not Verified After 2026-05-01 Changes

**Type:** Unverified production state
**Commit:** 7a47312 (2026-05-01) — DataSeeder expanded, HeroSection + AboutSection rewrites
**Severity:** MEDIUM — live site may not reflect current code

**What is known:** Commit 7a47312 was pushed to origin/main on 2026-05-01. The GitHub Actions
workflow (`deploy-portfolio.yml`) triggers on push to main. The workflow includes a verification
step that confirms the service is active, Nginx serves the frontend, and `/api/v1/health` responds.

**What is unknown:** Whether the 2026-05-01 deployment GitHub Actions run succeeded. The
workflow could have failed at any stage (build, SSH, service restart, or verification).

**Risk status:** UNVERIFIED — no session record confirms the production deployment succeeded

**To close this risk:**
1. Check GitHub Actions run history for the portfolio repository for the run triggered by 7a47312
2. Alternatively, curl https://faisalhanafi.com/api/v1/projects and verify the narrative content
   matches what refreshNarrativeContent() sets in DataSeeder (e.g., the CREAMS description
   should contain "unique children", not the original boilerplate)

---

## RISK-003: /api/v1/about Endpoint May Not Exist

**Type:** POSSIBLE DEFECT — frontend/backend contract mismatch
**Severity:** LOW — would cause silent frontend error; no AboutController found in class list

**What is known:** `frontend/src/api/client.ts` defines `api.getAbout()` which calls
`GET /api/v1/about`. The backend class list (verified 2026-05-10) contains no `AboutController`
or any controller mapping to `/api/v1/about`.

**What is unknown:** Whether the frontend actually calls this endpoint and whether it fails
silently (caught by the Axios error interceptor) or causes a visible error.

**Risk status:** UNVERIFIED — needs a browser network tab inspection or backend log review

**To close this risk:**
1. Run the backend locally and check if `GET http://localhost:8081/api/v1/about` returns 404
2. Check whether any frontend component calls `api.getAbout()`
3. If the endpoint is missing and the frontend calls it: either implement the backend endpoint
   or remove the frontend call
