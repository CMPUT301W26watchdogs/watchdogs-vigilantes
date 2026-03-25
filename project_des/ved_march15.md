# mar 15 - Fix Re-opened User Stories + UI Enhancement

## overview

four re-opened user stories fixed on branch `UI_Revamp+Code_Documentation`:
1. US 02.06.04 — organizer cancel entrants
2. US 02.02.02 — organizer map view of entrant locations
3. US 01.05.05 — entrant lottery criteria/guidelines
4. US 01.05.04 — entrant waitlist count

also enhanced the full UI to match `Project301_facelift.pdf` mockup.

---

## US 02.06.04 — cancel entrants that did not sign up

### what was missing
- `viewAttendee.java` was read-only, no way for organizer to cancel entrants

### what was done
- rewrote `viewAttendee.java` to use `EntrantAdapter` instead of `ProfileAdapter`
- `EntrantAdapter` now accepts an optional `eventId` — when provided, shows a "Cancel" button per entrant
- cancel button sets the attendee's Firestore status to `"cancelled"`
- added "Cancel All Pending" button to `attendee_list.xml` header for bulk cancellation
- added "Map" button in waiting list header to navigate to `EntrantMapActivity`


### merging

 ---
  US 02.06.04 — Organizer Cancel Entrants

  ┌─────────────────────┬───────────────────────────────────────────────────────────────────────────────────────────────────────────┐
  │        File         │                                                 Functions                                                 │
  ├─────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ viewAttendee.java   │ onCreate(), loadAttendees(), cancelPendingEntrants()                                                      │
  ├─────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ EntrantAdapter.java │ EntrantAdapter(List, String) ← 2nd constructor with eventId, onBindViewHolder() ← has cancel button logic │
  ├─────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ item_entrant.xml    │ cancelEntrantButton view                                                                                  │
  ├─────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ attendee_list.xml   │ cancel all button in header                                                                               │
  └─────────────────────┴───────────────────────────────────────────────────────────────────────────────────────────────────────────┘

  Critical dependency: If EntrantAdapter's second constructor (the one taking eventId) doesn't make it, cancel buttons won't show.

### testing
US 02.06.04 — Organizer Cancel Entrants

  Accounts needed: 1 organizer + 1 entrant

  1. Log in as entrant, sign up for any event → you're now "pending"
  2. Switch to organizer account
  3. Go to Your Events → tap the event → tap Waiting List
  4. You should see the entrant listed with a Cancel button next to their name
  5. Tap Cancel → their status changes to "cancelled" in Firestore
  6. Also test Cancel All Pending button in the header to bulk-cancel everyone

  What to verify: The entrant disappears from the waiting list after cancellation.


### files changed
- `viewAttendee.java` — full rewrite with cancel functionality
- `EntrantAdapter.java` — added second constructor with eventId, cancel button logic
- `item_entrant.xml` — added `cancelEntrantButton` MaterialButton
- `attendee_list.xml` — redesigned with header bar, map button, cancel all button

---

## US 02.02.02 — map where entrants joined waiting list from

### what was missing
- `EntrantMapActivity` existed but queried wrong collection (`waitingList` instead of `attendees`)
- geolocation was never captured during sign-up

### what was done
- `EntrantMapActivity.java` — fixed to query `attendees` collection (matches rest of codebase)
- removed placeholder/fake markers — now shows toast if no location data available
- `EventDetailActivity.java` — `performSignUp()` now captures device GPS coordinates using `FusedLocationProviderClient`
- if event has `geolocationRequired: true`, location permission is requested before sign-up
- latitude/longitude stored in attendee document in Firestore
- added `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` permissions to `AndroidManifest.xml`
- added `play-services-location:21.1.0` dependency to `build.gradle.kts`

### files changed
- `EntrantMapActivity.java` — query fix, removed placeholders
- `EventDetailActivity.java` — geolocation capture on sign-up
- `AndroidManifest.xml` — location permissions + missing activity declarations
- `app/build.gradle.kts` — play-services-location dependency

### merging

  ---
  US 02.02.02 — Organizer Map View of Entrant Locations

  ┌──────────────────────────┬────────────────────────────────────────────────────────────────────────────────────────┐
  │           File           │                                       Functions                                        │
  ├──────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────┤
  │ EntrantMapActivity.java  │ onCreate(), onMapReady()                                                               │
  ├──────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────┤
  │ EventDetailActivity.java │ performSignUp(), submitSignUp(), onRequestPermissionsResult() ← location captured here │
  ├──────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────┤
  │ AndroidManifest.xml      │ ACCESS_FINE_LOCATION + ACCESS_COARSE_LOCATION permissions                              │
  ├──────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────┤
  │ app/build.gradle.kts     │ play-services-location:21.1.0 dependency                                               │
  ├──────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────┤
  │ attendee_list.xml        │ Map button in header                                                                   │
  └──────────────────────────┴────────────────────────────────────────────────────────────────────────────────────────┘

  Critical dependency: If build.gradle.kts dependency or manifest permissions are missing, the whole sign-up with location flow crashes.

### testing

Accounts needed: 1 organizer + 1 entrant (with location permissions)

1. Log in as **entrant**, open any event → tap **Sign Up**
2. When prompted, **allow location permissions**
3. Switch to **organizer** account
4. Go to **Your Events** → tap the event → tap **Waiting List**
5. Tap the **Map** button in the waiting list header
6. The map should show a pin at the entrant's GPS location

What to verify: map shows real coordinates, not fake/placeholder pins. If no entrants granted location, a toast appears saying no location data available.

---

## US 01.05.05 — lottery selection criteria/guidelines

### what was missing
- `LotteryInfoActivity` showed event name, draw date, total spots — but no actual criteria or guidelines explaining how the lottery works

### what was done
- `LotteryInfoActivity.java` — added lottery criteria text dynamically:
  1. equal chance for all entrants on waiting list
  2. random selection of N entrants after registration closes
  3. decline → replacement drawn from remaining waitlist
  4. no priority based on sign-up time
  5. can cancel entry before draw
- also now shows total waitlist count from Firestore
- also checks `attendees` collection instead of `waitingList` for user status
- `activity_lottery_info.xml` — redesigned with stats row cards (Total Spots, On Waitlist, Draw Date), status section, and SELECTION CRITERIA section

### files changed
- `LotteryInfoActivity.java` — criteria text, waitlist count, fixed collection query
- `activity_lottery_info.xml` — full redesign with criteria section

### merging

  ---
  US 01.05.05 — Lottery Criteria/Guidelines

  ┌───────────────────────────┬────────────────────────────────────────────────────────────────┐
  │           File            │                           Functions                            │
  ├───────────────────────────┼────────────────────────────────────────────────────────────────┤
  │ LotteryInfoActivity.java  │ onCreate() ← entire criteria section is built here dynamically │
  ├───────────────────────────┼────────────────────────────────────────────────────────────────┤
  │ activity_lottery_info.xml │ stats row cards, status section, SELECTION CRITERIA section    │
  └───────────────────────────┴────────────────────────────────────────────────────────────────┘

  Critical dependency: It's all in onCreate() — if the layout doesn't match (missing TextView IDs), the criteria won't display.

### testing

Accounts needed: any entrant account

1. Log in as **entrant**, open any event
2. Tap the **Lottery Info** button on the event detail screen
3. You should see:
   - Stats row with **Total Spots**, **On Waitlist**, **Draw Date**
   - Your current **status** for this event
   - A **SELECTION CRITERIA** section listing all 5 rules

What to verify: all 5 criteria are shown, the On Waitlist count reflects the real number of pending attendees from Firestore (not hardcoded).

---

## US 01.05.04 — total entrants on waiting list

### what was missing
- `EventDetailActivity` had a `waitlistCount` TextView in layout but never populated it
- event cards in `EventAdapter` had `waitingCount` but never queried Firestore for it

### what was done
- `EventDetailActivity.java` — added `loadWaitlistCount()` method that queries `attendees` collection where `status == "pending"` and sets count
- `EventAdapter.java` — added Firestore query in `onBindViewHolder` to count pending attendees and display on each event card as "X Waiting"
- also shows location + date info on event cards

### files changed
- `EventDetailActivity.java` — waitlist count query
- `EventAdapter.java` — per-card waitlist count + location info

### merging

  ---
  US 01.05.04 — Waitlist Count

  ┌───────────────────────────┬───────────────────────────────────────────────────────────────────┐
  │           File            │                             Functions                             │
  ├───────────────────────────┼───────────────────────────────────────────────────────────────────┤
  │ EventDetailActivity.java  │ loadWaitlistCount()                                               │
  ├───────────────────────────┼───────────────────────────────────────────────────────────────────┤
  │ EventAdapter.java         │ onBindViewHolder() ← queries Firestore for pending count per card │
  ├───────────────────────────┼───────────────────────────────────────────────────────────────────┤
  │ activity_event_detail.xml │ waitlistCount TextView                                            │
  ├───────────────────────────┼───────────────────────────────────────────────────────────────────┤
  │ item_event.xml            │ waiting count TextView on event cards                             │
  └───────────────────────────┴───────────────────────────────────────────────────────────────────┘

  Critical dependency: loadWaitlistCount() is called inside onCreate() — if the TextView ID is missing from the layout, it silently fails.

### testing

Accounts needed: any account

**On the Event Detail screen:**
1. Open any event → look for the waiting count displayed in the stats area
2. It should show the current number of pending attendees (e.g. "3 Waiting")

**On the Events List screen:**
1. Go to **Upcoming Events**
2. Each event card should show **"X Waiting"** below the event title
3. Sign up with a second account, come back — the count should increment by 1

What to verify: the number is live from Firestore and only counts `status == "pending"` attendees, not accepted/declined/cancelled.

---

## UI enhancement (matching Project301_facelift.pdf)

### bottom navigation
- added `BottomNavigationView` with 4 tabs (Events, Home, Alerts, Profile) across all main screens
- created `res/menu/bottom_nav_menu.xml`
- wired up navigation in `HomePage`, `AllEventsActivity`, `ProfilePage`, `EventDetailActivity`, `AddEvent`

### screens updated

| Screen | Changes |
|---|---|
| Home | settings icon, icons in quick access cards, "My Alerts" card, bottom nav |
| Events List | VIGILANTE header, subtitle, category filter chips (All/Sports/Arts/Music), bottom nav |
| Event Detail | poster image at top, status badge overlay, icon-prefixed info rows, description card, side-by-side stats cards, registration date cards, waitlist integrity section, bottom nav |
| Profile | circle avatar with initial letter, stats row (Events/Selected/Waiting), Edit button in header, Delete Profile at bottom, bottom nav |
| Add Event | back arrow + title header, red ADD EVENT banner, sectioned form (BASIC INFO/SCHEDULE/REGISTRATION), Material TextInputLayouts, side-by-side fields, bottom nav |
| Admin | horizontal back arrow + title layout |
| Register | role toggle buttons (Entrant/Organizer/Admin) replacing checkbox |
| Event Cards | MaterialCardView, status badges, location info, waiting/spots counts |

### new drawables
- `bg_circle_avatar.xml`, `bg_status_badge.xml`, `bg_status_waiting.xml`, `bg_status_closed.xml`, `bg_status_selected.xml`
- `bg_chip_selected.xml`, `bg_chip_unselected.xml`, `bg_role_selected.xml`, `bg_role_unselected.xml`
- `bg_add_event_header.xml`

### new colors
- `status_selected` (#2196F3), `status_declined` (#E74C3C)
- `nav_icon_inactive` (#999999), `nav_icon_active` (#C62828), `bottom_nav_bg` (#FAFAFA)

### code cleanup
- removed ALL comments from all 20 Java files
- removed XML comments from layout files
- removed comments from build.gradle.kts

---

## all files changed (re-opened stories + UI)

**java (20 files):** all files in `com.example.vigilante` — comments removed + UI/logic updates where needed
**layouts:** `homepage.xml`, `allevents.xml`, `activity_event_detail.xml`, `profile_page.xml`, `activity_add_event.xml`, `adminpage.xml`, `register_page.xml`, `item_event.xml`, `item_entrant.xml`, `attendee_list.xml`, `activity_lottery_info.xml`, `item_profile.xml`
**values:** `colors.xml`, `themes.xml`
**drawables:** 10 new drawable XMLs
**menu:** `bottom_nav_menu.xml`
**config:** `AndroidManifest.xml`, `build.gradle.kts`

---
---

# mar 15 (part 2) — 5 new user stories

## overview

five new user stories implemented on branch `UI_Revamp+Code_Documentation`:
1. US 01.01.04 — entrant filter events by interests/availability
2. US 01.02.03 — entrant event history
3. US 01.04.03 — entrant opt out of notifications
4. US 01.05.01 — replacement draw when selected user declines
5. US 02.05.01 — organizer notify chosen entrants

---

## US 01.01.04 — filter events by interests and availability

### what was missing
- category filter chips (All/Sports/Arts/Music) existed in `allevents.xml` but were purely visual — no click handlers or filtering logic
- `Event.java` had no `category` field
- `AddEvent.java` had a category input field (`event_category`) but never saved it to Firestore

### what was done
- added `category` field to `Event.java` with getter/setter
- `AddEvent.java` now reads `event_category` input and saves it to Firestore as `category`
- `AllEventsActivity.java` — full rewrite of filtering logic:
  - maintains `allEventsList` (unfiltered) and `eventList` (filtered for display)
  - `setupChipListeners()` wires click handlers on all 4 chips
  - `updateChipStyles()` toggles `bg_chip_selected`/`bg_chip_unselected` per active filter
  - `applyFilter()` filters `allEventsList` by category (case-insensitive match)
  - filter applies across all fetch modes (all, org, admin)

### files changed
- `Event.java` — added `category` field
- `AddEvent.java` — saves category to Firestore
- `AllEventsActivity.java` — chip filtering logic

### merging
---
  US 01.01.04 — Filter Events by Category

  ┌────────────────────────┬─────────────────────────────────────────────────────────┐
  │          File          │                        Functions                        │
  ├────────────────────────┼─────────────────────────────────────────────────────────┤
  │ AllEventsActivity.java │ setupChipListeners(), updateChipStyles(), applyFilter() │
  ├────────────────────────┼─────────────────────────────────────────────────────────┤
  │ Event.java             │ getCategory(), setCategory()                            │
  ├────────────────────────┼─────────────────────────────────────────────────────────┤
  │ AddEvent.java          │ saveEventToFirestore() ← saves category field           │
  ├────────────────────────┼─────────────────────────────────────────────────────────┤
  │ allevents.xml          │ chip views (chipAll, chipSports, chipArts, chipMusic)   │
  └────────────────────────┴─────────────────────────────────────────────────────────┘

 Critical dependency: Event.java needs the category field. If that getter/setter is missing, filtering silently shows nothing.

### testing

Accounts needed: 1 organizer + 1 entrant

1. Log in as **organizer**, create 2 events — give one category "Sports", one category "Music"
2. Switch to **entrant** account
3. Go to **Upcoming Events**
4. Tap the **Sports** chip — only the Sports event should remain visible
5. Tap the **Music** chip — only the Music event should appear
6. Tap **All** — both events return
7. Tap **Arts** — list should be empty (no Arts events)

What to verify: chip highlights red when active, list updates immediately without navigating away, "All" always restores the full list.

---

## US 01.02.03 — event history (registered events, selected or not)

### what was missing
- no way for entrants to see a history of events they registered for and their selection status

### what was done
- created `EventHistoryActivity.java` — queries all events, then for each event checks if the current user has an attendee document; collects matching events with their status (pending/selected/accepted/declined/cancelled)
- created `EventHistoryAdapter.java` — RecyclerView adapter that displays event title, date, and a color-coded status badge; tapping a row opens `EventDetailActivity`
- status badge colors: selected=blue, accepted=green, pending=orange, declined/cancelled=gray
- added "Event History" button to `profile_page.xml` between phone section and organizer buttons
- button wired in `ProfilePage.java`
- profile stats row (Events/Selected/Waiting) now populated from Firestore via `loadProfileStats()`

### files changed
- `EventHistoryActivity.java` — new file
- `EventHistoryAdapter.java` — new file
- `activity_event_history.xml` — new layout with back arrow, subtitle, RecyclerView, bottom nav
- `item_event_history.xml` — new card layout for history items
- `ProfilePage.java` — event history button, profile stats query, notification toggle
- `profile_page.xml` — event history button, notification toggle section
- `AndroidManifest.xml` — registered `EventHistoryActivity`


### merging
 ---
  US 01.02.03 — Event History

  ┌────────────────────────────┬──────────────────────────────────────────────────────────────────────────────────────────┐
  │            File            │                                        Functions                                         │
  ├────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────────────┤
  │ EventHistoryActivity.java  │ onCreate(), loadHistory(), setupBottomNav()                                              │
  ├────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────────────┤
  │ EventHistoryAdapter.java   │ constructor, onCreateViewHolder(), onBindViewHolder(), getItemCount(), HistoryViewHolder │
  ├────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────────────┤
  │ ProfilePage.java           │ onCreate() ← event history button wired here, loadProfileStats()                         │
  ├────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────────────┤
  │ activity_event_history.xml │ entire layout                                                                            │
  ├────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────────────┤
  │ item_event_history.xml     │ card layout                                                                              │
  ├────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────────────┤
  │ AndroidManifest.xml        │ EventHistoryActivity registered                                                          │
  └────────────────────────────┴──────────────────────────────────────────────────────────────────────────────────────────┘

  Critical dependency: If EventHistoryActivity isn't in the manifest, the app crashes when tapping "Event History" on profile.

### testing

Accounts needed: 1 entrant

1. Log in as **entrant**, sign up for 2–3 events (you'll be "pending" on each)
2. Go to **Profile** → tap **Event History**
3. You should see a list of those events with an orange **Pending** badge on each
4. Have an organizer run the lottery — one of your events moves to "selected"
5. Go back to Event History — that event should now show a blue **Selected** badge
6. Accept the invitation — badge changes to green **Accepted**
7. Decline a different event — badge changes to gray **Declined**

What to verify: status badges are color-coded correctly, tapping any row opens the event detail screen, profile stats row (Events / Selected / Waiting) shows accurate counts.

---

## US 01.04.03 — opt out of receiving notifications

### what was missing
- no way for entrants to disable notifications from organizers/admins

### what was done
- added `SwitchMaterial` toggle to `profile_page.xml` in a NOTIFICATIONS section
- `ProfilePage.java` reads `notificationsEnabled` boolean from Firestore user doc and sets toggle state
- toggle change listener updates Firestore `notificationsEnabled` field
- when organizer sends notifications (US 02.05.01), `viewAttendee.java` checks each user's `notificationsEnabled` flag — skips users who opted out
- `drawReplacementFromWaitlist()` in `EventDetailActivity.java` also creates notifications — respecting the flag would require an additional check at the notification display level (handled by the NotificationsActivity query)

### files changed
- `profile_page.xml` — notification toggle section
- `ProfilePage.java` — toggle read/write logic
- `viewAttendee.java` — checks `notificationsEnabled` before sending


### merging
  ---
  US 01.04.03 — Opt Out of Notifications

  ┌───────────────────┬──────────────────────────────────────────────────────────────────────────────────────────────────┐
  │       File        │                                            Functions                                             │
  ├───────────────────┼──────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ ProfilePage.java  │ onCreate() ← toggle read/write wired here                                                        │
  ├───────────────────┼──────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ viewAttendee.java │ sendNotificationToSelected(), sendNotificationToWaiting() ← both check notificationsEnabled flag │
  ├───────────────────┼──────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ profile_page.xml  │ NOTIFICATIONS section with SwitchMaterial toggle                                                 │
  └───────────────────┴──────────────────────────────────────────────────────────────────────────────────────────────────┘

### testing

Accounts needed: 1 entrant + 1 organizer

1. Log in as **entrant**, go to **Profile** → scroll to the NOTIFICATIONS section
2. Toggle notifications **off**
3. Switch to **organizer**, open an event → Waiting List → tap **Notify Waiting**
4. Send a custom message
5. Switch back to **entrant** → go to **Alerts** — the notification should **not** appear
6. Go back to Profile, toggle notifications **on**
7. Organizer sends another notification
8. Switch to entrant → Alerts — notification **does** appear this time

What to verify: the toggle state persists after closing and reopening the app, opted-out users receive no notifications even when the organizer explicitly targets them.

---

## US 01.05.01 — replacement draw when selected user declines

### what was missing
- when a selected user was shown as "selected" in `EventDetailActivity`, the only options were a disabled button — no way to accept or decline
- no mechanism to automatically draw a replacement from the waitlist

### what was done
- `EventDetailActivity.java`:
  - when status is `"selected"`, shows Accept and Decline buttons (hides Sign Up button)
  - `acceptInvitation()` updates status to `"accepted"`
  - `declineInvitation()` updates status to `"declined"` then calls `drawReplacementFromWaitlist()`
  - `drawReplacementFromWaitlist()` queries all `"pending"` attendees, randomly picks one, sets them to `"selected"`, and creates a notification in Firestore
  - also handles `"accepted"` and `"declined"` display states
- `activity_event_detail.xml` — added Accept/Decline button row (green/red MaterialButtons) in a horizontal LinearLayout, both `visibility="gone"` by default
- `EventAdapter.java` — updated to handle `"accepted"`, `"declined"` statuses with appropriate badge colors and button text

### files changed
- `EventDetailActivity.java` — accept/decline/redraw logic
- `activity_event_detail.xml` — accept/decline buttons
- `EventAdapter.java` — additional status handling

### merging
  US 01.05.01 — Replacement Draw When Declined

  ┌───────────────────────────┬────────────────────────────────────────────────────────────────────────┐
  │           File            │                               Functions                                │
  ├───────────────────────────┼────────────────────────────────────────────────────────────────────────┤
  │ EventDetailActivity.java  │ acceptInvitation(), declineInvitation(), drawReplacementFromWaitlist() │
  ├───────────────────────────┼────────────────────────────────────────────────────────────────────────┤
  │ activity_event_detail.xml │ accept/decline button row (both visibility="gone" by default)          │
  ├───────────────────────────┼────────────────────────────────────────────────────────────────────────┤
  │ EventAdapter.java         │ onBindViewHolder() ← handles "accepted" / "declined" status badges     │
  └───────────────────────────┴────────────────────────────────────────────────────────────────────────┘

  Critical dependency: All three functions form a chain — decline → drawReplacement → notification. If any one is missing, the flow breaks.

### testing

Accounts needed: 2 entrants + 1 organizer

1. Both entrants sign up for the same event (both are "pending")
2. Log in as **organizer**, run lottery selecting 1 — one entrant is now "selected"
3. Switch to the **selected entrant**, open that event → you should see **Accept** and **Decline** buttons (Sign Up button hidden)
4. Tap **Accept** → status updates to "accepted", buttons disappear, status badge shows Accepted
5. Repeat steps 1–3 with a fresh setup, but this time tap **Decline**
6. Status updates to "declined"
7. Switch to the **second entrant** (still "pending") → they should now be "selected" and receive a notification

What to verify: after declining, the replacement draw happens automatically from the remaining pending entrants and a notification lands in their Alerts tab.

---

## US 02.05.01 — organizer send notification to chosen entrants

### what was missing
- no way for organizer to notify selected entrants that they've been chosen

### what was done
- `viewAttendee.java`:
  - added "Draw Lottery" button visible on waiting list view — opens dialog for organizer to enter number of entrants to select, then `performLotteryDraw()` randomly selects N pending entrants and sets them to `"selected"`
  - added "Notify Selected Entrants" button visible on selected entrants view
  - `sendNotificationToSelected()` iterates over all `"selected"` attendees, checks each user's `notificationsEnabled` preference, and creates a notification document in Firestore `notifications` collection with userId, eventId, title, message, timestamp, read=false
- created `NotificationsActivity.java` — displays all notifications for current user from Firestore `notifications` collection, ordered by timestamp descending; tapping a notification marks it as read and opens the event
- wired "Alerts" tab in bottom navigation across all activities to open `NotificationsActivity`
- "My Alerts" card on HomePage now navigates to `NotificationsActivity`
- `attendee_list.xml` — added Draw Lottery button (blue) and Notify Selected button (green)

### files changed
- `viewAttendee.java` — lottery draw + notification sending
- `attendee_list.xml` — draw lottery and notify buttons
- `NotificationsActivity.java` — new file with embedded NotificationAdapter
- `activity_notifications.xml` — new layout matching VIGILANTE header style
- `item_notification.xml` — new card layout for notification items
- `HomePage.java` — alerts card navigates to NotificationsActivity
- all activities with bottom nav — added `nav_alerts` handler
- `AndroidManifest.xml` — registered `NotificationsActivity`

### merging
---
  US 02.05.01 — Notify Chosen Entrants (Organizer)

  ┌───────────────────────────────┬─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
  │             File              │                                                                          Functions                                                                          │
  ├───────────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ viewAttendee.java             │ showDrawLotteryDialog(), performLotteryDraw(), sendNotificationToSelected()                                                                                 │
  ├───────────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ NotificationsActivity.java    │ onCreate(), loadNotifications(), setupBottomNav(), NotificationAdapter (inner class) with onCreateViewHolder(), onBindViewHolder(), getItemCount(),         │
  │                               │ NotifViewHolder                                                                                                                                             │
  ├───────────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ activity_notifications.xml    │ entire layout                                                                                                                                               │
  ├───────────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ item_notification.xml         │ card layout                                                                                                                                                 │
  ├───────────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ attendee_list.xml             │ Draw Lottery button, Notify Selected button                                                                                                                 │
  ├───────────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ AndroidManifest.xml           │ NotificationsActivity registered                                                                                                                            │
  ├───────────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ HomePage.java                 │ onCreate() ← My Alerts card wired                                                                                                                           │
  ├───────────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ All activities with bottom    │ setupBottomNav() ← nav_alerts handler                                                                                                                       │
  │ nav                           │                                                                                                                                                             │
  └───────────────────────────────┴─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘

### testing

Accounts needed: 2 entrants + 1 organizer

1. Both entrants sign up for the same event (both "pending")
2. Log in as **organizer**, open event → Waiting List → tap **Draw Lottery**
3. Enter "1" in the dialog → tap Draw — one entrant moves to "selected"
4. Tap **Notify Selected Entrants** button
5. Switch to the **selected entrant** → go to **Alerts** tab
6. A notification "You've been selected!" should appear with the event name
7. Tap the notification — it should mark as read and open the event detail

What to verify: only selected entrants receive the notification (not pending ones), the Alerts tab badge/count updates, tapping the notification navigates correctly.

---

# mar 15 (part 3) — 5 more user stories + tests

## overview

five additional user stories implemented on branch `UI_Revamp+Code_Documentation`, plus unit tests and espresso tests for all five:
1. US 02.05.03 — organizer draw replacement from waiting pool
2. US 02.06.03 — organizer see final enrolled entrants list
3. US 02.06.05 — organizer export enrolled list as CSV
4. US 02.07.01 — organizer send notification to waiting list entrants
5. US 02.07.02 — organizer send notification to selected entrants



## US 02.05.03 — draw replacement from waiting pool

### what was done
- added "Draw Replacement" button to `attendee_list.xml` (blue, visible only in selected view)
- `viewAttendee.java` → `drawReplacementFromWaitlist()` queries pending attendees, randomly selects one, updates status to "selected", and sends a notification to the chosen replacement
- notification includes event title fallback ("an event" if null)

### files changed
- `viewAttendee.java` — `drawReplacementFromWaitlist()` method
- `attendee_list.xml` — `draw_replacement_button`


### merging
  ---
  US 02.05.03 — Draw Replacement from Waiting Pool (Organizer)

  ┌───────────────────┬───────────────────────────────┐
  │       File        │           Functions           │
  ├───────────────────┼───────────────────────────────┤
  │ viewAttendee.java │ drawReplacementFromWaitlist() │
  ├───────────────────┼───────────────────────────────┤
  │ attendee_list.xml │ draw_replacement_button       │
  └───────────────────┴───────────────────────────────┘

### testing

Accounts needed: 3 entrants + 1 organizer

1. All 3 entrants sign up for the same event (all "pending")
2. Log in as **organizer**, run lottery selecting 1 — one entrant is "selected"
3. Open event → **Selected Entrants** view → tap **Draw Replacement** button
4. One of the remaining pending entrants should move to "selected"
5. That entrant should receive a notification in their Alerts tab

What to verify: the Draw Replacement button is only visible in the Selected view (not Waiting), a new entrant is randomly chosen from the pending pool, and a "You've been selected!" notification arrives.

---

## US 02.06.03 — see final list of enrolled entrants

### what was done
- added "Enrolled" button to organizer event cards in `item_event.xml` (orgButtonRow2)
- `EventAdapter.java` — wired `viewAttendeeEnrolled` button to launch `viewAttendee` with type "enrolled"
- `viewAttendee.java` — new "enrolled" type branch in `onCreate()` sets title "Enrolled Entrants", loads attendees with status "accepted", shows export CSV button

### files changed
- `item_event.xml` — `viewAttendeeEnrolled` button
- `EventAdapter.java` — enrolled button click handler
- `viewAttendee.java` — enrolled type handling

### merging
 ---
  US 02.06.03 — See Final Enrolled List (Organizer)

  ┌───────────────────┬────────────────────────────────────────────────────┐
  │       File        │                     Functions                      │
  ├───────────────────┼────────────────────────────────────────────────────┤
  │ viewAttendee.java │ onCreate() ← "enrolled" type branch                │
  ├───────────────────┼────────────────────────────────────────────────────┤
  │ EventAdapter.java │ onBindViewHolder() ← enrolled button click handler │
  ├───────────────────┼────────────────────────────────────────────────────┤
  │ item_event.xml    │ viewAttendeeEnrolled button                        │
  └───────────────────┴────────────────────────────────────────────────────┘

### testing

Accounts needed: 2 entrants + 1 organizer

1. Both entrants sign up for an event, organizer runs lottery, both accept their invitations (status = "accepted")
2. Log in as **organizer** → go to **Your Events**
3. On the event card, tap the **Enrolled** button
4. You should see a list titled "Enrolled Entrants" showing only the accepted attendees
5. Pending/declined/cancelled entrants should not appear in this list

What to verify: the title says "Enrolled Entrants" (not "Waiting List"), only accepted-status entrants are shown, the Export CSV button is visible on this view.

---

## US 02.06.05 — export enrolled entrants list as CSV

### what was done
- added "Export CSV" button to `attendee_list.xml` (orange, visible only in enrolled view)
- `viewAttendee.java` → `exportEnrolledCsv()` builds CSV string, writes to cache dir, shares via `FileProvider` intent
- `escapeCsvField()` and `buildCsvContent()` are static helper methods (testable without Android context)
- added `FileProvider` declaration to `AndroidManifest.xml`
- created `res/xml/file_paths.xml` for FileProvider paths config

### files changed
- `viewAttendee.java` — `exportEnrolledCsv()`, `escapeCsvField()`, `buildCsvContent()`
- `attendee_list.xml` — `export_csv_button`
- `AndroidManifest.xml` — FileProvider
- `res/xml/file_paths.xml` — new file

### merging
---
  US 02.06.05 — Export Enrolled List as CSV (Organizer)

  ┌────────────────────────┬──────────────────────────────────────────────────────────┐
  │          File          │                        Functions                         │
  ├────────────────────────┼──────────────────────────────────────────────────────────┤
  │ viewAttendee.java      │ exportEnrolledCsv(), escapeCsvField(), buildCsvContent() │
  ├────────────────────────┼──────────────────────────────────────────────────────────┤
  │ attendee_list.xml      │ export_csv_button                                        │
  ├────────────────────────┼──────────────────────────────────────────────────────────┤
  │ AndroidManifest.xml    │ FileProvider declaration                                 │
  ├────────────────────────┼──────────────────────────────────────────────────────────┤
  │ res/xml/file_paths.xml │ entire new file — FileProvider paths config              │
  └────────────────────────┴──────────────────────────────────────────────────────────┘
  Critical dependency: file_paths.xml is easy to miss — without it, sharing the CSV crashes with a FileUriExposedException.

### testing

Accounts needed: 1 organizer with at least 2 accepted entrants

1. Log in as **organizer** → Your Events → tap **Enrolled** on an event with accepted entrants
2. Tap the **Export CSV** button (orange)
3. A share sheet should appear — share it to Files, Gmail, or any app
4. Open the CSV — it should have a header row `Name,Email,Status` and one row per enrolled entrant

What to verify: names/emails with commas are wrapped in quotes (escaped correctly), the file opens cleanly, the Export CSV button is only visible in the Enrolled view and hidden in Waiting/Selected views.

---

## US 02.07.01 — send notification to all waiting list entrants

### what was done
- added "Notify Waiting" button to `attendee_list.xml` (green, visible only in waiting view)
- `viewAttendee.java` → `showNotifyWaitingDialog()` opens AlertDialog with EditText for custom message
- `sendNotificationToWaiting()` iterates all pending attendees, checks each user's `notificationsEnabled` preference (opt-out respected), creates notification docs in Firestore

### files changed
- `viewAttendee.java` — `showNotifyWaitingDialog()`, `sendNotificationToWaiting()`
- `attendee_list.xml` — `notify_waiting_button`

### merging
  ---
  US 02.07.01 — Notify Waiting List Entrants (Organizer)

  ┌───────────────────┬────────────────────────────────────────────────────────┐
  │       File        │                       Functions                        │
  ├───────────────────┼────────────────────────────────────────────────────────┤
  │ viewAttendee.java │ showNotifyWaitingDialog(), sendNotificationToWaiting() │
  ├───────────────────┼────────────────────────────────────────────────────────┤
  │ attendee_list.xml │ notify_waiting_button                                  │
  └───────────────────┴────────────────────────────────────────────────────────┘

### testing

Accounts needed: 2 entrants + 1 organizer

1. Both entrants sign up for an event (both "pending")
2. Log in as **organizer** → Your Events → tap **Waiting List** on the event
3. Tap **Notify Waiting** button (green)
4. A dialog appears — type a custom message (e.g. "Registration closes tomorrow!") → tap Send
5. Switch to each **entrant** account → go to **Alerts** tab
6. Both should have received the custom message notification

What to verify: the Notify Waiting button is only visible in the Waiting view (not Selected/Enrolled), both pending entrants get the message, any entrant who opted out of notifications does NOT receive it.

---

## US 02.07.02 — send notification to all selected entrants

### what was done
- `viewAttendee.java` → `sendNotificationToSelected()` iterates all selected attendees, checks opt-out preference, sends "You've been selected!" notification with event title and accept/decline instructions
- "Notify Selected" button already existed in `attendee_list.xml`, now wired to this method

### files changed
- `viewAttendee.java` — `sendNotificationToSelected()`

### merging
  ---
  US 02.07.02 — Notify Selected Entrants (Organizer)

  ┌───────────────────┬───────────────────────────────────────────────────┐
  │       File        │                     Functions                     │
  ├───────────────────┼───────────────────────────────────────────────────┤
  │ viewAttendee.java │ sendNotificationToSelected()                      │
  ├───────────────────┼───────────────────────────────────────────────────┤
  │ attendee_list.xml │ notify_selected_button already existed, now wired │
  └───────────────────┴───────────────────────────────────────────────────┘

### testing

Accounts needed: 2 entrants + 1 organizer (one entrant selected, one still pending)

1. Run lottery so 1 entrant is "selected", 1 remains "pending"
2. Log in as **organizer** → Your Events → tap **Selected Entrants** on the event
3. Tap **Notify Selected** button
4. Switch to the **selected entrant** → go to **Alerts** tab
5. Should see a "You've been selected!" notification with the event title and accept/decline instructions
6. Switch to the **pending entrant** → go to **Alerts** tab
7. Should have received **no** notification

What to verify: only selected-status entrants receive this notification (not pending), the message includes the event title and instructions to accept or decline, opted-out users are skipped.

---

## tests written

### unit tests (5 test files, 38 test methods)

| Test file | US | Tests |
|---|---|---|
| `ReplacementDrawTest.java` | 02.05.03 | selectsOneFromPool, emptyPool_returnsNull, notInAlreadySelectedSet, singleEntrantPool, multipleDraws_depletesPool, changesStatusToSelected |
| `EnrolledListTest.java` | 02.06.03 | filterAccepted_returnsOnlyEnrolled, correctNames, excludesPending, excludesCancelledAndDeclined, emptyList_returnsEmpty, noAccepted_returnsEmpty, allAccepted_returnsAll |
| `CsvExportTest.java` | 02.06.05 | escapeCsvField (simple, comma, quote, newline, null, empty), buildCsvContent (header, data, lineCount, nullName, specialChar, emptyList) |
| `WaitingListNotificationTest.java` | 02.07.01 | containsAllFields, nullEventTitle_fallback, customMessagePreserved, defaultUnread, optOutCheck_false, optInCheck_true, nullDefaultsToEnabled |
| `SelectedNotificationTest.java` | 02.07.02 | hasCorrectTitle, messageIncludesEventTitle, nullTitle_fallback, messageIncludesAcceptDecline, defaultUnread, containsIds, multipleUsersIndependent |

### espresso tests (5 test files, 16 test methods)

| Test file | US | Tests |
|---|---|---|
| `ReplacementDrawEspressoTest.java` | 02.05.03 | showsDrawReplacementButton, hidesDrawReplacementButton (waiting view), drawReplacement_selectsPendingEntrant (Firestore verify) |
| `EnrolledListEspressoTest.java` | 02.06.03 | showsCorrectTitle, showsExportCsvButton, showsRecyclerView, hidesDrawReplacementButton |
| `CsvExportEspressoTest.java` | 02.06.05 | exportButtonHasCorrectText, exportButtonVisible, selectedView_exportButtonHidden |
| `NotifyWaitingEspressoTest.java` | 02.07.01 | showsNotifyButton, hidesNotifyWaitingButton (selected view), createsNotificationInFirestore (dialog + send + Firestore verify) |
| `NotifySelectedEspressoTest.java` | 02.07.02 | showsNotifySelectedButton, hidesNotifySelectedButton (waiting view), createsNotificationInFirestore (Firestore verify) |

---

## all files changed (part 3)

**modified java:** `viewAttendee.java`, `EventAdapter.java`
**modified layouts:** `attendee_list.xml`, `item_event.xml`
**modified config:** `AndroidManifest.xml`
**new files:** `res/xml/file_paths.xml`
**new unit tests:** `ReplacementDrawTest.java`, `EnrolledListTest.java`, `CsvExportTest.java`, `WaitingListNotificationTest.java`, `SelectedNotificationTest.java`
**new espresso tests:** `ReplacementDrawEspressoTest.java`, `EnrolledListEspressoTest.java`, `CsvExportEspressoTest.java`, `NotifyWaitingEspressoTest.java`, `NotifySelectedEspressoTest.java`
