# Unveil — Architecture Diagrams

---

## Core: Unveil, Plugins, and Host connect

```
╔═══════════════════════════════════════════════════════════════════════╗
║                           HOST APPLICATION                            ║
║                                                                       ║
║   Unveil.configure {              UnveilHost(                         ║
║       register(NetworkPlugin())       enabled = Unveil.isEnabled      ║
║       register(LogsPlugin())      ) {                                 ║
║   }                                   AppContent()                    ║
║                                   }                                   ║
║   Unveil.enable()                                                     ║
╚═══════════════╤══════════════════════════════╤════════════════════════╝
                │                              │
        configure { }                   isEnabled / wraps
                │                              │
                ▼                              ▼
    ┌───────────────────────┐      ┌─────────────────────────────────┐
    │        Unveil         │      │           UnveilHost            │
    │      (singleton)      │      │          (Composable)           │
    │                       │      │                                 │
    │  isEnabled: Boolean ──┼──────┘   ┌───────────────────────┐     │
    │  registry ────────────┼─────────►│     App Content       │     │
    └───────────┬───────────┘  reads   │  (movableContentOf)   │     │
                │              plugins └───────────────────────┘     │
                │                               +                    │
                ▼                      ┌───────────────────────┐     │
    ┌───────────────────────┐          │     UnveilDrawer      │     │
    │     UnveilConfig      │          │      (overlay)        │     │
    │    (DSL receiver)     │          └───────────────────────┘     │
    │                       │      └─────────────────────────────────┘
    │  register(plugin) ────┼──┐
    └───────────────────────┘  │ register
                                ▼
                ┌────────────────────────────────────┐
                │           UnveilRegistry           │
                │             (internal)             │
                │                                    │
                │  plugins: List<UnveilPlugin>       │
                │  register()  — deduplicates by id  │
                └────────────────────────────────────┘


Feature modules implement the UnveilPlugin interface:

    ┌──────────────────────────────────────────────────────┐
    │               <<interface>>  UnveilPlugin            │
    │                                                      │
    │  id: String            — unique key (snake_case)     │
    │  title: String         — shown in plugin list        │
    │  icon: UnveilIcon      — Builtin(name) | Emoji(ch)   │
    │  quickActions: List<QuickAction>   (default empty)   │
    │                                                      │
    │  @Composable                                         │
    │  Content(scope: UnveilPanelScope)                    │
    └──────────────────────────────────────────────────────┘
               ▲               ▲               ▲
               │               │               │
    ┌──────────┴────┐  ┌───────┴───────┐  ┌───┴────────────────┐
    │ NetworkPlugin │  │  LogsPlugin   │  │ FeatureFlagsPlugin │
    │ (unveil-net)  │  │ (unveil-logs) │  │     (unveil-ff)    │
    └───────────────┘  └───────────────┘  └────────────────────┘
                │               │               │
                └───────────────┴───────────────┘
                                │
                  each plugin receives a scope
                                │
                                ▼
               ┌────────────────────────────────┐
               │    <<interface>>               │
               │     UnveilPanelScope           │
               │                                │
               │  pushPage(title, content)      │
               │  popPage()                     │
               └────────────────────────────────┘
                 (created by DrawerController —
                  isolates plugins from internals)
```

---

## Drawer

```
UnveilHost
│
├── remembers ────────────────────────────────────────────────────────────┐
│                                                                         │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │                        DrawerController                         │   │
│   │                      (@Stable, internal)                        │   │
│   │                                                                 │   │
│   │   translationXFraction: Animatable<Float>                       │   │
│   │     0f = fully closed          1f = fully open                  │   │
│   │                                                                 │   │
│   │   pageStack: SnapshotStateList<DrawerPage>                      │   │
│   │     [0]  PluginList  ← always present, never removed            │   │
│   │     [1]  PluginPage  ← pushed by navigateTo(plugin)             │   │
│   │     [2]  SubPage     ← pushed via UnveilPanelScope              │   │
│   │                                                                 │   │
│   │   currentPage → pageStack.last()                                │   │
│   └──────────────────────┬──────────────────────────────────────────┘   │
│          writes          │              reads                           │
│         ┌────────────────┴─────────────────────┐                        │
│         │                                      │                        │
│         ▼                                      ▼                        │
│  ┌──────────────────────┐           ┌──────────────────────────────┐    │
│  │  Modifier            │           │        UnveilDrawer          │    │
│  │  .drawerGesture()    │           │      (overlay composable)    │    │
│  │                      │           │                              │    │
│  │  Detects swipe from  │           │  graphicsLayer {             │    │
│  │  right edge (40 dp)  │           │    translationX =            │    │
│  │  or anywhere if open │           │      width*(1 - fraction)    │    │
│  │                      │           │  }                           │    │
│  │  drag  → snapTo()    │           │                              │    │
│  │  release             │           │  UnveilThemeProvider {       │    │
│  │    > 0.4f → open()   │           │                              │    │
│  │    ≤ 0.4f → close()  │           │    Scrim  (when isOpen)      │    │
│  │             +        │           │      clickable → close()     │    │
│  │    resetToPluginList │           │                              │    │
│  └──────────────────────┘           │    AnimatedContent(          │    │
│                                     │      targetState =           │    │
│  ┌──────────────────────┐           │        currentPage           │    │
│  │  UnveilGestureExclu  │           │    ) { page ->               │    │
│  │  sionEffect          │           │      when (page) {           │    │
│  │                      │           │        PluginList ───────┐   │    │
│  │  Registers right     │           │        PluginPage ───────┤   │    │
│  │  edge as excluded    │           │        SubPage    ───────┘   │    │
│  │  from system back    │           │      }                       │    │
│  │  gesture (Android)   │           │    }                         │    │
│  └──────────────────────┘           │  }                           │    │
│                                     └──────────────────────────────┘    │
│  ┌──────────────────────┐                                               │
│  │    DrawerHandle      │     ← visible only when drawer is closed      │
│  │  (right-edge tab)    │       click → open()                          │
│  └──────────────────────┘                                               │
│                                                                         │
└─────────────────── DrawerController created here ───────────────────────┘


DrawerPage — sealed navigation state:

    ┌─────────────────┬───────────────────────┬───────────────────────┐
    │   PluginList    │     PluginPage        │       SubPage         │
    │  (data object)  │    (data class)       │    (data class)       │
    │                 │                       │                       │
    │  Root screen    │  plugin: UnveilPlugin │  title: String        │
    │  always at [0]  │                       │  content: @Composable │
    │                 │  DrawerBackHeader     │                       │
    │  Shows all      │  + plugin.Content(    │  DrawerBackHeader     │
    │  plugins and    │      scope)           │  + page.content()     │
    │  quick actions  │                       │                       │
    └─────────────────┴───────────────────────┴───────────────────────┘
         stack[0]              stack[1]              stack[2]
                                                    (max depth)


Page transitions (AnimatedContent):

    forward  (deeper into stack):
      incoming ──[ slides in  from right ]──►
      outgoing ──[ slides out to   left  ]──►

    backward (toward PluginList):
      incoming ◄──[ slides in  from left  ]
      outgoing ◄──[ slides out to   right ]


Back handling:

    pageStack.size > 1 ──► UnveilBackHandler enabled
         │
    system back / back button
         │
         ├── navigateBack() ──► pops last DrawerPage
         │
         └── if already at [PluginList] ──► close() drawer


Theme tokens (composition local, scoped to drawer):

    UnveilThemeProvider  ← wraps entire UnveilDrawer
         │
         ├── LocalUnveilColors  ──► UnveilTheme.colors
         │                            surface, primary, scrim,
         │                            chip*, onSurface*, ...
         │
         └── LocalUnveilTypography ──► UnveilTheme.typography
                                          drawerTitle, body,
                                          sectionTitle, mono, ...
```
