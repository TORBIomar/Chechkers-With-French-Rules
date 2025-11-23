# How to Change the Window Icon/Logo

## Steps:

1. **Prepare your icon image:**
   - Format: PNG (recommended) or ICO
   - Size: 16x16, 32x32, 64x64, or 128x128 pixels (or multiple sizes)
   - Name it: `icon.png`

2. **Place the file:**
   - Put your `icon.png` file in: `src/main/resources/com/example/dames/`
   - The full path should be: `src/main/resources/com/example/dames/icon.png`

3. **That's it!**
   - The code will automatically load and use your icon
   - The icon will appear in:
     - Window title bar
     - Taskbar (when minimized)
     - Alt+Tab switcher

## Alternative Formats:

If you want to use a different format or name, you can modify `Main.java`:

```java
// For ICO file:
Image icon = new Image(getClass().getResourceAsStream("/com/example/dames/icon.ico"));

// For different name:
Image icon = new Image(getClass().getResourceAsStream("/com/example/dames/logo.png"));
```

## Recommended Icon Sizes:

For best results, provide multiple sizes:
- 16x16 (small)
- 32x32 (medium)
- 64x64 (large)
- 128x128 (high DPI)

JavaFX will automatically use the best size for each context.

