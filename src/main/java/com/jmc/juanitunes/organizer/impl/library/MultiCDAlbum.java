package com.jmc.juanitunes.organizer.impl.library;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.jmc.juanitunes.organizer.api.library.Album;
import com.jmc.juanitunes.organizer.api.library.Song;

public class MultiCDAlbum implements Album {
	
	private final String name;
	private Set<Album> albums = new HashSet<Album>();
	
	public MultiCDAlbum(	String name,
							Set<Album> albums) {
		this.name = name;
		this.albums.addAll(albums);
	}

	public Album addSong(Song song) {
		albums.stream()
			  .filter(a -> a.getName().equals(song.getCatalogNumber()))
			  .findFirst()
			  .get()
			  .addSong(song);
		return this;
	}

	public Album removeSong(Song song) {
		albums.stream()
			  .filter(a -> a.getName().equals(song.getCatalogNumber()))
			  .findFirst()
			  .get()
			  .removeSong(song);
		return this;
	}

	public Album sort(Comparator<Song>... criterion) {
		albums.stream()
			  .forEach(a -> a.sort(criterion));
		return this;
	}
	
	public Album merge(Album other) {
		
		albums.stream()
			  .filter(a -> a.equals(other))
			  .forEach(a -> a.merge(other));
		
		return this;
	}

	public Optional<Album> match(String string) {
		Set<Album> matching = albums.stream()
									 .map(a -> a.match(string))
									 .filter(a-> a.isPresent())
									 .map(a -> a.get())
									 .collect(Collectors.toSet());
		
		if(matching.isEmpty()) return Optional.empty();
		
		return Optional.of(new MultiCDAlbum(name, matching));
	}

	public String getName() {
		return name;
	}

	public String getYear() {
		
		List<String> years = albums.stream()
								   .map(Album::getYear)
								   .distinct()
								   .collect(Collectors.toList());
		
		return (years.size() == 1) ? years.get(0) : "-";
	}

	public String getCatalogNumber() {
		return "-";
	}

	public int getDurationInSeconds() {
		return albums.stream()
					  .mapToInt(Album::getDurationInSeconds)
					  .sum();
	}

	public double getSizeInMegaBytes() {
		return albums.stream()
					  .mapToDouble(Album::getSizeInMegaBytes)
					  .sum();
	}
	
	public Set<Song> getSongs() {
		return albums.stream()
					  .map(Album::getSongs)
					  .flatMap(Set::stream)
					  .collect(Collectors.toSet());
	}
	
	@Override
	public String toString() {
		StringBuilder albumStr = new StringBuilder();
		
		albumStr = albumStr.append("MultiCD Album Name: " + name).append(System.lineSeparator());
		albumStr = albumStr.append("Contents:").append(System.lineSeparator());
		albumStr.append(
				 	albums.stream()
				 		  .map(Album::toString)
				 		  .collect(Collectors.joining(System.lineSeparator()))
				 );

		return albumStr.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == null) return false;
		if(!(other instanceof MultiCDAlbum)) return false;
		
		MultiCDAlbum otherAlbum = (MultiCDAlbum) other;
		if(!otherAlbum.getName().equals(name)) return false;
		
		return true;			
	}

}
