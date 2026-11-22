import { expect, test } from '@playwright/test';

test('unknown URLs offer a working route home', async ({ page }) => {
  await page.goto('/unknown-page');
  await expect(page.getByRole('heading', { name: 'Page Not Found' })).toBeVisible();
  await page.getByRole('link', { name: 'Go Home', exact: true }).click();
  await expect(page).toHaveURL('/');
  await expect(page.locator('#about')).toBeAttached();
});
