INSERT INTO Artist(id, name, biography, image) VALUES (1, 'ArtistTest', 'bla', 'C:\\'); -- for songdao testing
INSERT INTO Artist(id, name, biography, image) VALUES (2, 'ArtistTest2', 'bla', 'C:\\'); -- for songdao testing
INSERT INTO Artist(id, name, biography, image) VALUES (3, 'ArtistTest3', 'bla', 'C:\\'); -- for songdao testing
INSERT INTO Artist(id, name, biography, image) VALUES (DEFAULT, 'TestName', 'TestBiography', 'C:\\');

INSERT INTO Album(id, name, release_year, artist_id, cover) VALUES ( 1 , 'AlbumTest', 2004, 1, 'C:\\'); -- for songdao testing
INSERT INTO Album(id, name, release_year, artist_id, cover) VALUES ( DEFAULT , 'Album', 2004, 1, 'C:\\');

INSERT INTO Song(id, filepath, name, length, artist_id, album_id, genre, rating, lyrics) VALUES(DEFAULT, 'C://', 'SongTest', 30, 1, 1, 'pop', 4, 'bla');

INSERT INTO Playlist(id, name) VALUES (DEFAULT , 'Test');

INSERT INTO Statistic (id, date , song_id) VALUES (DEFAULT , DEFAULT, 1);