const fs = require('fs');
const path = require('path');

const filesToCopy = [
  'index.html',
  'style.css',
  'app.js',
  'ai-engine.js',
  'i18n.js',
  'slider.js',
  'lucide.min.js',
  'manifest.json',
  'sw.js',
  'icon.png',
  'sample_portrait.jpg',
  'sample_vintage.jpg',
  'qr_code.png'
];

const destDir = path.join(__dirname, 'www');
if (!fs.existsSync(destDir)) {
  fs.mkdirSync(destDir, { recursive: true });
}

for (const file of filesToCopy) {
  const src = path.join(__dirname, file);
  const dest = path.join(destDir, file);
  if (fs.existsSync(src)) {
    fs.copyFileSync(src, dest);
  }
}

console.log('Successfully prepared www/ directory for Capacitor!');
