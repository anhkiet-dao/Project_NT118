package com.example.do_an.presentation.reading.reader;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.do_an.R;
import com.example.do_an.domain.reading.repository.FavoriteRepository;

import java.util.Locale;
import java.util.Map;

public class FavoriteHandler {
    private final Context context;
    private final FavoriteRepository favoriteRepository;

    public FavoriteHandler(Context context) {
        this.context = context;
        favoriteRepository = new FavoriteRepository(context);
    }

    public void setLocale(Locale locale) {
        favoriteRepository.setLocale(locale);
    }

    private String getFavoriteTitle(String mainStoryTitle, String currentTitle) {
        if (mainStoryTitle != null && !mainStoryTitle.equals(currentTitle)) {
            return mainStoryTitle + " - " + currentTitle;
        }
        return (mainStoryTitle != null) ? mainStoryTitle : currentTitle;
    }

    public void checkIfFavorite(String storyId, String mainTitle, String currentTitle, String userEmail, ImageView btnFavorite) {
        if (userEmail == null || storyId == null) return;

        final String titleToCheck = getFavoriteTitle(mainTitle, currentTitle);

        favoriteRepository.getFavorites(userEmail, favorites -> {
            boolean isFavorite = false;
            for (Map<String, Object> item : favorites) {
                if (item != null) {
                    String id = (String) item.get("storyId");
                    String title = (String) item.get("title");
                    if (storyId.equals(id) && titleToCheck.equals(title)) {
                        isFavorite = true;
                        break;
                    }
                }
            }

            final boolean finalIsFavorite = isFavorite;
            btnFavorite.post(() -> {
                btnFavorite.setTag(finalIsFavorite);
                btnFavorite.setImageResource(finalIsFavorite
                        ? R.drawable.ic_favorite_filled
                        : R.drawable.ic_favorite_border);
                btnFavorite.invalidate();
            });
        });
    }

    public void toggleFavorite(String userEmail, String storyId, String mainTitle, String currentTitle,
                               String author, String category, String imageUrl, String readUrl,
                               ImageView btnFavorite) {
        if (userEmail == null || storyId == null || readUrl == null) {
            Toast.makeText(context, "Thiếu thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        final String titleForFavorite = getFavoriteTitle(mainTitle, currentTitle);

        Object tagValue = btnFavorite.getTag();
        boolean isFavorite = (tagValue instanceof Boolean) && (Boolean) tagValue;

        if (!isFavorite) {
            favoriteRepository.addFavorite(userEmail, storyId, titleForFavorite, author, category, null, imageUrl, readUrl);

            btnFavorite.post(() -> {
                btnFavorite.setTag(true);
                btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
                btnFavorite.invalidate();
            });
        } else {
            favoriteRepository.removeFavorite(userEmail, storyId, titleForFavorite);

            btnFavorite.post(() -> {
                btnFavorite.setTag(false);
                btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                btnFavorite.invalidate();
            });
        }
    }
}
