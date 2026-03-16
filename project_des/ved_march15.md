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

---

## all files changed (5 new stories)

**new java files:** `EventHistoryActivity.java`, `EventHistoryAdapter.java`, `NotificationsActivity.java`
**modified java:** `Event.java`, `AddEvent.java`, `AllEventsActivity.java`, `EventDetailActivity.java`, `EventAdapter.java`, `ProfilePage.java`, `viewAttendee.java`, `HomePage.java`
**new layouts:** `activity_event_history.xml`, `item_event_history.xml`, `activity_notifications.xml`, `item_notification.xml`
**modified layouts:** `activity_event_detail.xml`, `profile_page.xml`, `attendee_list.xml`
**config:** `AndroidManifest.xml`

---
---

# mar 15 (part 3) — 5 more user stories + tests

## overview

five additional user stories implemented on branch `UI_Revamp+Code_Documentation`, plus unit tests and espresso tests for all five:
1. US 02.05.03 — organizer draw replacement from waiting pool
2. US 02.06.03 — organizer see final enrolled entrants list
3. US 02.06.05 — organizer export enrolled list as CSV
4. US 02.07.01 — organizer send notification to waiting list entrants
5. US 02.07.02 — organizer send notification to selected entrants

---

## US 02.05.03 — draw replacement from waiting pool

### what was done
- added "Draw Replacement" button to `attendee_list.xml` (blue, visible only in selected view)
- `viewAttendee.java` → `drawReplacementFromWaitlist()` queries pending attendees, randomly selects one, updates status to "selected", and sends a notification to the chosen replacement
- notification includes event title fallback ("an event" if null)

### files changed
- `viewAttendee.java` — `drawReplacementFromWaitlist()` method
- `attendee_list.xml` — `draw_replacement_button`

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

---

## US 02.07.01 — send notification to all waiting list entrants

### what was done
- added "Notify Waiting" button to `attendee_list.xml` (green, visible only in waiting view)
- `viewAttendee.java` → `showNotifyWaitingDialog()` opens AlertDialog with EditText for custom message
- `sendNotificationToWaiting()` iterates all pending attendees, checks each user's `notificationsEnabled` preference (opt-out respected), creates notification docs in Firestore

### files changed
- `viewAttendee.java` — `showNotifyWaitingDialog()`, `sendNotificationToWaiting()`
- `attendee_list.xml` — `notify_waiting_button`

---

## US 02.07.02 — send notification to all selected entrants

### what was done
- `viewAttendee.java` → `sendNotificationToSelected()` iterates all selected attendees, checks opt-out preference, sends "You've been selected!" notification with event title and accept/decline instructions
- "Notify Selected" button already existed in `attendee_list.xml`, now wired to this method

### files changed
- `viewAttendee.java` — `sendNotificationToSelected()`

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
