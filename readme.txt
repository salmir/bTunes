created by: Lena Lenz


README:

Jede Entity ist eine Info.
Ein InfoOwner ist eine Info, die andere Infos beinhaltet - wie z.B. ein Song.
Ein Song ist 'owner' eines Albums und eines Artists.
mit 'song.set(album)' fügt man ein Album einem Song hinzu, gleichzeitig wird beim entsprechenden Album der Song automatisch hinzugefügt und als 'owner' gespeichert. (analog für Artist)

Artists und Alben sind keine InfoOwners sondern DelegatingInfos. Diese übernehmen die Aufgabe des automatischen Hinzufügens.
Dh. ihr müsst einfach nur 'song.set(album)' bzw. 'song.set(artist)' schreiben und im Hintergrund läuft der Rest.
Mit 'album.get(SONG)' holt man sich dann alle Songs aus einem Album. (analog mit Artist)
Genre ist bei uns keine Entity dh wir erstellen diese immer nur zur Laufzeit.

DTOs sind dummy objects, die wir herumdelegieren. Die speichern wir dann in der DB. (siehe convert-Methode in der Library)
Mit 'createNew()' bekommen wir dann die eigentlichen Entity-Objekte


todo: im sql ordner bitte eure Sql-create-statements von eurer jeweiligen Entity eintragen


***************************************
***	examples of how to use the code ***
***************************************

Song x = new Song(0, "song X", new File("C:/"));
Song y = new Song(1, "song Y", new File("C:/"));
Song z = new Song(2, "song Z", new File("C:/"));
Song a = new Song(3, "song A", new File("C:/"));
Artist someArtist = new Artist(0, "some artist");
Artist otherArtist = new Artist(0, "other artist");
Genre rock = new Genre("Rock");
Album foo = new Album(0, "foo");
Album bar = new Album(1, "bar");

x.set(someArtist);
x.set(foo);
x.set(rock);

y.set(otherArtist);
y.set(foo);
y.set(rock);

z.set(otherArtist);
z.set(foo);

a.set(otherArtist);
a.set(bar);

Formatter singleLineFormatter = new Formatter(", ", "=", "'");
Formatter multiLineFormatter = new Formatter("\n\t", ": ");

System.out.println("songs by artist '"+someArtist.getName()+"'");
for(Song s : someArtist.get(SONG)){
	System.out.println("\t"+singleLineFormatter.format(s));
}

System.out.println("\nall songs in same album as '"+x.getName()+"'");
for(Song s : x.get(ALBUM).get(SONG)){
	System.out.println("\t"+singleLineFormatter.format(s));
}

System.out.println("\nformatting single song:");
System.out.println(multiLineFormatter.format(x));


******* output *******

songs by artist 'some artist'
	Song='song X', Genre='Rock', Artist='some artist', Album='foo'

all songs in same album as 'song X'
	Song='song X', Genre='Rock', Artist='some artist', Album='foo'
	Song='song Y', Genre='Rock', Artist='other artist', Album='foo'
	Song='song Z', Artist='other artist', Album='foo'

formatting single song:
Song: song X
	Genre: Rock
	Artist: some artist
	Album: foo

************************