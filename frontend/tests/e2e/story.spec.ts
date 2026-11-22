import { expect, test } from '@playwright/test';

test('story chapters support arrow, Home and End keys with one tab stop', async ({ page }) => {
  await page.goto('/#about');
  const tabs = page.getByRole('tablist', { name: 'Professional story chapters' }).getByRole('tab');
  const first = tabs.first();
  const second = tabs.nth(1);
  const last = tabs.last();
  await first.focus();

  await first.press('ArrowRight');
  await expect(second).toBeFocused();
  await expect(second).toHaveAttribute('aria-selected', 'true');
  await expect(page.getByRole('tabpanel')).toHaveAttribute('aria-labelledby', await second.getAttribute('id') as string);
  await expect(page.getByRole('tabpanel')).toContainText('Make Messy Data Trustworthy First');

  await second.press('End');
  await expect(last).toBeFocused();
  await expect(last).toHaveAttribute('aria-selected', 'true');
  await last.press('ArrowRight');
  await expect(first).toBeFocused();
  await first.press('ArrowLeft');
  await expect(last).toBeFocused();
  await last.press('Home');
  await expect(first).toBeFocused();
  await expect(first).toHaveAttribute('aria-selected', 'true');
  await expect(page.getByRole('tablist').locator('[role="tab"][tabindex="0"]')).toHaveCount(1);
});
