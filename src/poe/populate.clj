;; Poe
;; Author: Christopher Olsen
;; Copyright 2013
;; License: GPLv3 (a copy should be included, if not visit 
;;          http://www.gnu.org/licenses/gpl.txt


;; this file contains poetry in the public domain to populate the database
;; for testing and demonstration purposes gathered from 
;; www.publicdomainpoems.com
;; this does not fall under the GNU GPLv3

(ns poe.populate
  (:gen-class)
  (:require [clojure.java.jdbc :as j]
            [clojure.java.jdbc.sql :as s]))

(def db
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname "db/database.db"})

(defn no-table? [table-name]
  ;; pull all the table names (returned as a map), check membership with 'some'
    (let [tables (j/query 
                  db
                  ["SELECT name FROM sqlite_master WHERE type = \"table\""])]
      (nil? (some #(= table-name %)
                  (map :name tables)))))
  
(defn create-db []
  (j/with-connection db
    (do
      (if (no-table? "author")
        (j/create-table "author"
                        [:name :text "PRIMARY KEY"]))
      (if (no-table? "collection")
        (j/create-table "collection"
                        [:name :text]
                        [:year "INTEGER"]
                        [:type :text]
                        [:author :text "references author (name)"]
                        ["PRIMARY KEY" "(name, author)"]))
      (if (no-table? "poem")
        (j/create-table "poem"
                        [:name :text]
                        [:body :text]
                        [:collection :text "references collection (name)"]
                        [:author :text "references author (name)"]
                        ["PRIMARY KEY" "(name, collection)"]))
      nil)))

(def auth-data1 {:name "Walt Whitman"})
(def auth-data2 {:name "Robert Frost"})
(def coll-data1 {:name "Various Public Works"
                 :author "Walt Whitman"})
(def coll-data2 {:name "Public Works"
                 :author "Robert Frost"})
(def poem-data11 {:author "Walt Whitman"
                  :collection "Various Public Works"
                  :name "O Captain! My Captain!"
                  :body (str "O Captain! my Captain! our fearful trip is done,\n"
                             "The ship has weather'd every rack, the prize we sought is won,\n"
                             "The port is near, the bells I hear, the people all exulting,\n"
                             "While follow eyes the steady keel, the vessel grim and daring;\n"
                             "     But O heart! heart! heart!\n"
                             "       O the bleeding drops of red,\n"
                             "         Where on the deck my Captain lies,\n"
                             "            Fallen cold and dead.\n"
                             "\n"
                             "O Captain! my Captain! rise up and hear the bells;\n"
                             "Rise up--for you the flag is flung--for you the bugle trills,\n"
                             "For you bouquets and ribbon'd wreaths--for you the shores a-crowding,\n"
                             "For you they call, the swaying mass, their eager faces turning;\n"
                             "      Here Captain! dear father!\n"
                             "       This arm beneath your head!\n"
                             "         It is some dream that on the deck,\n"
                             "            You've fallen cold and dead.\n"
                             "\n"
                             "My Captain does not answer, his lips are pale and still,\n"
                             "My father does not feel my arm, he has no pulse nor will,\n"
                             "The ship is anchor'd safe and sound, its voyage closed and done,\n"
                             "From fearful trip the victor ship comes in with object won;\n"
                             "     Exult O shores, and ring O bells!\n"
                             "       But I with mournful tread,\n"
                             "         Walk the deck my Captain lies,\n"
                             "           Fallen cold and dead.")}) 

(def poem-data12 {:author "Walt Whitman"
                  :collection "Various Public Works"
                  :name "I saw in Louisiana a live-oak growing."
                  :body (str "I saw in Louisiana a live-oak growing,\n"
                             "All alone stood it and the moss hung down from the branches,\n"
                             "Without any companion it grew there uttering joyous leaves of\n"
                             "       dark green,\n"
                             "And its look, rude, unbending, lusty, made me think of myself,\n"
                             "But I wonder'd how it could utter joyous leaves standing alone\n"
                             "      there without its friend near, for I knew I could not,\n"
                             "\n"
                             "And I broke off a twig with a certain number of leaves upon it,\n"
                             "      and twined around it a little moss,\n"
                             "And brought it away, and I have placed it in sight in my room,\n"
                             "It is not needed to remind me as of my own dear friends,\n"
                             "(For I believe lately I think of little else than of them,)\n"
                             "Yet it remains to me a curious token, it makes me think of manly\n"
                             "      love;\n"
                             "For all that, and though the live-oak glistens there in Louisiana\n"
                             "       solitary in a wide flat space,\n"
                             "Uttering joyous leaves all its life without a friend a lover near,\n"
                             "I know very well I could not.")})

(def poem-data13 {:author "Walt Whitman"
                  :collection "Various Public Works"
                  :name "The Prairie-Grass Dividing."
                  :body (str "The prairie-grass dividing, its special odor breathing,\n"
                             "I demand of it the spiritual corresponding,\n"
                             "Demand the most copious and close companionship of men,\n"
                             "Demand the blades to rise of words, acts, beings,\n"
                             "Those of the open atmosphere, coarse, sunlit, fresh, nutritious,\n"
                             "Those that go their own gait, erect, stepping with freedom and\n"
                             "       command, leading not following,\n"
                             "Those with a never-quell'd audacity, those with sweet and lusty\n"
                             "       flesh clear of taint,\n"
                             "Those that look carelessly in the faces of Presidents and governors,\n"
                             "       as to say Who are you? Those of earth-born passion, simple, never constrain'd, never\n"
                             "       obedient,\n"
                             "Those of inland America.")})

(def poem-data14 {:author "Walt Whitman"
                  :collection "Various Public Works"
                  :name "A Farm Picture."
                  :body (str "Through the ample open door of the peaceful country barn,\n"
                             "A sunlit pasture field with cattle and horses feeding,\n"
                             "And haze and vista, and the far horizon fading away.")})

(def poem-data15 {:author "Walt Whitman"
                  :collection "Various Public Works"
                  :name "1861"
                  :body (str "Arm'd year-year of the struggle,\n"
                             "No dainty rhymes or sentimental love verses for you terrible year,\n"
                             "Not you as some pale poetling seated at a desk lisping cadenzas\n"
                             "       piano,\n"
                             "But as a strong man erect, clothed in blue clothes, advancing,\n"
                             "       carrying a rifle on your shoulder,\n"
                             "With well-gristled body and sunburnt face and hands, with a knife\n"
                             "       in the belt at your side,\n"
                             "As I heard you shouting loud, your sonorous voice ringing across\n"
                             "       the continent,\n"
                             "Your masculine voice O year, as rising amid the great cities,\n"
                             "Amid the men of Manhattan I saw you as one of the workmen,\n"
                             "       the dwellers in Manhattan,\n"
                             "Or with large steps crossing the prairies out of Illinois and\n"
                             "       Indiana,\n"
                             "Rapidly crossing the West with springy gait and descending the\n"
                             "       Alleghanies,\n"
                             "Or down from the great lakes or in Pennsylvania, or on deck\n"
                             "       along the Ohio river,\n"
                             "Or southward along the Tennessee or Cumberland rivers, or at\n"
                             "       Chattanooga on the mountain top,\n"
                             "Saw I your gait and saw I your sinewy limbs clothed in blue,\n"
                             "       bearing weapons, robust year,")})

(def poem-data21 {:author "Robert Frost"
                  :collection "Public Works"
                  :name "The Road Not Taken"
                  :body (str "Two roads diverged in a yellow wood,\n"
                             "And sorry I could not travel both\n"
                             "And be one traveler, long I stood\n"
                             "And looked down one as far as I could\n"
                             "To where it bent in the undergrowth;\n"
                             "\n"
                             "Then took the other, as just as fair,\n"
                             "And having perhaps the better claim,\n"
                             "Because it was grassy and wanted wear;\n"
                             "Though as for that the passing there\n"
                             "Had worn them really about the same,\n"
                             "\n"
                             "And both that morning equally lay\n"
                             "In leaves no step had trodden black.\n"
                             "Oh, I kept the first for another day!\n"
                             "Yet knowing how way leads on to way,\n"
                             "I doubted if I should ever come back.\n"
                             "\n"
                             "I shall be telling this with a sigh\n"
                             "Somewhere ages and ages hence:\n"
                             "Two roads diverged in a wood, and I—\n"
                             "I took the one less traveled by,\n"
                             "And that has made all the difference.\n")}) 


(def poem-data22 {:author "Robert Frost"
                  :collection "Public Works"
                  :name "After Apple Picking"
                  :body (str "My long two-pointed ladder’s sticking through a tree\n"
                             "Toward heaven still,\n"
                             "And there’s a barrel that I didn’t fill\n"
                             "Beside it, and there may be two or three\n"
                             "Apples I didn’t pick upon some bough.\n"
                             "But I am done with apple-picking now.\n"
                             "Essence of winter sleep is on the night,\n"
                             "The scent of apples: I am drowsing off.\n"
                             "I cannot rub the strangeness from my sight\n"
                             "I got from looking through a pane of glass\n"
                             "I skimmed this morning from the drinking trough\n"
                             "And held against the world of hoary grass.\n"
                             "It melted, and I let it fall and break.\n"
                             "But I was well\n"
                             "Upon my way to sleep before it fell,\n"
                             "And I could tell\n"
                             "What form my dreaming was about to take.\n"
                             "Magnified apples appear and disappear,\n"
                             "Stem end and blossom end,\n"
                             "And every fleck of russet showing clear.\n"
                             "My instep arch not only keeps the ache,\n"
                             "It keeps the pressure of a ladder-round.\n"
                             "I feel the ladder sway as the boughs bend.\n"
                             "And I keep hearing from the cellar bin\n"
                             "The rumbling sound 25\n"
                             "Of load on load of apples coming in.\n"
                             "For I have had too much\n"
                             "Of apple-picking: I am overtired\n"
                             "Of the great harvest I myself desired.\n"
                             "There were ten thousand thousand fruit to touch,\n"
                             "Cherish in hand, lift down, and not let fall.\n"
                             "For all\n"
                             "That struck the earth,\n"
                             "No matter if not bruised or spiked with stubble,\n"
                             "Went surely to the cider-apple heap\n"
                             "As of no worth.\n"
                             "One can see what will trouble\n"
                             "This sleep of mine, whatever sleep it is.\n"
                             "Were he not gone,\n"
                             "The woodchuck could say whether it’s like his\n"
                             "Long sleep, as I describe its coming on,\n"
                             "Or just some human sleep.")})

;; (defn authors []
;;   [auth-data1])
;; (defn collections []
;;   [coll-data1])
;; (defn poems []
;;   [poem-data11 poem-data12])

(defn populate []
  (println "\n start populate \n")
  (j/insert! db :author auth-data1 auth-data2)
  (println "\n after authors \n")
  (j/insert! db :collection coll-data1 coll-data2)
  (println "\n after collections \n")
  (j/insert! db :poem poem-data11 poem-data12 poem-data13
                      poem-data14 poem-data15
                      poem-data21 poem-data22))

(defn initialize []
  (create-db)
  (populate))

(initialize)