package be.ceau.podcastfinder.persist.store;

import org.junit.Test;

public class Cleaner {

	@Test
	public void clean() {
		
		
		
//		List<LegacyPodcastLink> legacyList = null;
//		try (LegacyLiteStore legacyStore = new LegacyLiteStore()) {
//			legacyList = legacyStore.getFullPodcastLinks().stream().filter(Objects::nonNull).collect(Collectors.toList());
//		}
//
//		try (FoundFeedInfoStore fullStore = new FoundFeedInfoStore()) {
//			try (BatchingLinkStore<FoundFeedInfo> store = new BatchingLinkStore<>(fullStore)) {
//				legacyList.stream()
//						.map(p -> {
//							try {
//								return new FoundFeedInfo(-1, p.getUri(), p.getName(), StringUtils.substring(p.getLanguage(), 0, 2), null, p.getInsertDate());
//							} catch (IllegalArgumentException e) {
//								e.printStackTrace();
//								return null;
//							}
//						})
//						.filter(Objects::nonNull)
//						.forEach(store::add);
//				
//				final Map<URI, Integer> urlToId = fullStore.list()
//						.stream()
//						.collect(Collectors.toMap(FoundFeedInfo::getUri, FoundFeedInfo::getPodcastId));
				
//				try (StatusSQLiteStore statusStore = new StatusSQLiteStore()) {
//					try (BatchingStatusStore batchStatusStore = new BatchingStatusStore(statusStore)) {
//						legacyList.stream()
//								.filter(l -> l.getLastFailDate() != null || l.getLastValidDate() != null)
//								.map(l -> {
//									LocalDateTime date;
//									String status;
//									if (l.getLastFailDate() != null) {
//										date = l.getLastFailDate();
//										status = PodcastStatus.FAIL;
//									} else {
//										date = l.getLastValidDate();
//										status = PodcastStatus.SUCCESS;
//									}
//									Integer podcastId = urlToId.get(l.getUri());
//									if (podcastId == null) {
//										System.err.println("no podcastId for URI " + l.getUri());
//										return null;
//									}
//									return PodcastStatus.newInstance(urlToId.get(l.getUri()), status, date);
//								})
//								.filter(Objects::nonNull)
//								.forEach(batchStatusStore::add);
//					}
//				}
//			}
//		}

//		try (LegacyITunesStore itunesStore = new LegacyITunesStore()) {
//			
//			try (FoundFeedStore coreStore = new FoundFeedStore()) {
//				try (BatchingLinkStore<FoundFeed> store = new BatchingLinkStore<>(coreStore)) {
//			
//					itunesStore.getLinks()
//						.forEach(store::add);
//					
//				}
//			}
//		}
//
		
	}

}
