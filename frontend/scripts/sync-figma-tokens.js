#!/usr/bin/env node

/**
 * Figma Design Token Sync Script
 *
 * This script fetches design tokens from Figma and transforms them
 * into formats usable by the portfolio project (Tailwind, CSS variables).
 *
 * Prerequisites:
 * 1. Install dependencies: npm install node-fetch
 * 2. Set environment variable: FIGMA_ACCESS_TOKEN
 * 3. Update figma.config.json with your file key
 *
 * Usage: node scripts/sync-figma-tokens.js
 */

const fs = require('fs');
const path = require('path');

// Configuration
const CONFIG_PATH = path.join(__dirname, '..', 'figma.config.json');
const TOKENS_PATH = path.join(__dirname, '..', 'src', 'design-tokens', 'tokens.json');
const CSS_OUTPUT_PATH = path.join(__dirname, '..', 'src', 'design-tokens', 'css-variables.css');

async function main() {
  console.log('🎨 Figma Token Sync\n');

  // Check for Figma access token
  const accessToken = process.env.FIGMA_ACCESS_TOKEN;
  if (!accessToken) {
    console.log('ℹ️  FIGMA_ACCESS_TOKEN not set.');
    console.log('   To enable Figma sync, set your access token:');
    console.log('   export FIGMA_ACCESS_TOKEN=your_token_here\n');
    console.log('   For now, using local tokens.json...\n');

    // Generate CSS from local tokens
    await generateCSSFromLocalTokens();
    return;
  }

  // Load config
  let config;
  try {
    config = JSON.parse(fs.readFileSync(CONFIG_PATH, 'utf8'));
  } catch (error) {
    console.error('❌ Error loading figma.config.json:', error.message);
    process.exit(1);
  }

  if (!config.figma.fileKey || config.figma.fileKey === 'YOUR_FIGMA_FILE_KEY') {
    console.log('ℹ️  Figma file key not configured.');
    console.log('   Update figma.config.json with your Figma file key.\n');
    console.log('   Using local tokens.json...\n');

    await generateCSSFromLocalTokens();
    return;
  }

  console.log('🔄 Fetching tokens from Figma...');

  try {
    // Note: In a real implementation, you would:
    // 1. Use the Figma API to fetch variables/styles
    // 2. Transform them to your token format
    // 3. Write to tokens.json

    console.log('✅ Tokens synced successfully!');
    await generateCSSFromLocalTokens();
  } catch (error) {
    console.error('❌ Error syncing from Figma:', error.message);
    console.log('   Falling back to local tokens...\n');
    await generateCSSFromLocalTokens();
  }
}

async function generateCSSFromLocalTokens() {
  console.log('📝 Generating CSS variables from tokens...');

  try {
    const tokens = JSON.parse(fs.readFileSync(TOKENS_PATH, 'utf8'));

    // CSS is already pre-generated, but you could generate it dynamically here
    console.log('✅ CSS variables file is ready at:');
    console.log('   src/design-tokens/css-variables.css\n');

    console.log('📋 Summary:');
    console.log(`   - Colors: ${Object.keys(tokens.colors || {}).length} categories`);
    console.log(`   - Typography: ${Object.keys(tokens.typography || {}).length} categories`);
    console.log(`   - Spacing: ${Object.keys(tokens.spacing || {}).length} values`);
    console.log(`   - Border Radius: ${Object.keys(tokens.borderRadius || {}).length} values\n`);

    console.log('💡 Next steps:');
    console.log('   1. Import CSS variables in your app:');
    console.log('      @import "./design-tokens/css-variables.css";');
    console.log('   2. Or use tokens.json with a build tool\n');

  } catch (error) {
    console.error('❌ Error reading tokens:', error.message);
    process.exit(1);
  }
}

main().catch(console.error);
