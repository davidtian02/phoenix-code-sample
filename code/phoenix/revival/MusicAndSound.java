import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;

public class MusicAndSound {
	private static MediaPlayer mediaPlayerUi;
	private static boolean isMediaPlayerUiPrepared = false;
	
	private static MediaPlayer mediaPlayerResults;
	private static boolean isMediaPlayerResultsPrepared = false;
	
	private static long timerEnemyGettingHitDamaged;
	private static long timerSoundEffectCD = 100;
	private static long timerHeroBullet;
	private static long timerHeroBulletCD = 10;
	private static long timerHeroGettingHit;
	private static long timerEnemyGettingHitNotDamaged;
	private static long timerShipExplosion;
	private static long timerEnemyBullet;
	private static long timerCoin;
	
	//game stuff
	static Sound gameOverSound;
	
	//ambiance
	static Sound explosionSound;
	static Sound coinSound;
	
	//hero
	static Sound heroBulletSound;
	static Sound heroGettingHitSound;
	static Sound heroLevelUpSound;
	static Sound heroItemUsedSound;
	
	//enemy
	static Sound enemyFiringSound;
	static Sound enemyGettingHitDamagedSound;
	static Sound enemyGettingHitNotDamagedSound;
	
	public static void initializeMusic(Context ctx){
		mediaPlayerUi = new MediaPlayer();
		mediaPlayerResults = new MediaPlayer();
		
		AssetFileDescriptor descriptor;
		try {
			descriptor = ctx.getAssets().openFd("music/background_ui.ogg");
			mediaPlayerUi.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
			mediaPlayerUi.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO
				}
			});
			mediaPlayerUi.prepareAsync();
			mediaPlayerUi.setLooping(true);
			
			descriptor = ctx.getAssets().openFd("music/background_results.ogg");
			mediaPlayerResults.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
			mediaPlayerResults.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					isMediaPlayerResultsPrepared = true;
					mediaPlayerResults.start();
					mediaPlayerResults.pause();
				}
			});
			mediaPlayerResults.prepareAsync();
			mediaPlayerResults.setLooping(true);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	public static void playMusicUi(){
		if(isMediaPlayerUiPrepared && !mediaPlayerUi.isPlaying()){
			try{
				mediaPlayerUi.start();
			} catch(IllegalStateException ise){
				ise.printStackTrace();
			}
		}
	}
	
	public static void pauseMusicUi(){
		if(mediaPlayerUi.isPlaying() && isMediaPlayerUiPrepared){
			try{
				mediaPlayerUi.pause();
			} catch(IllegalStateException ise){
				ise.printStackTrace();
			}
		}
	}
	
	public static void playMusicResults(){
		if(isMediaPlayerResultsPrepared && !mediaPlayerResults.isPlaying()){
			try{
				mediaPlayerResults.start();
			} catch(IllegalStateException ise){
				ise.printStackTrace();
			}
		}
	}
	
	public static void pauseMusicResults(){
		if(mediaPlayerResults.isPlaying() && isMediaPlayerResultsPrepared){
			try{
				mediaPlayerResults.stop();
			} catch(IllegalStateException ise){
				ise.printStackTrace();
			}
		}
	}
	
	public static void tearDown(){
		if(isMediaPlayerUiPrepared){
			isMediaPlayerUiPrepared = false;
			mediaPlayerUi.stop();
			mediaPlayerUi.release();
		}
		
		if(isMediaPlayerResultsPrepared){
			isMediaPlayerResultsPrepared = false;
			mediaPlayerResults.stop();
			mediaPlayerResults.release();
		}
	}
	
	//============================= Sound Effects ==================================
	public static void loadSoundEffects(Game game){
		
		gameOverSound = game.getAudio().newSound("sound/effects/game_over_voice.ogg");
		
		explosionSound = game.getAudio().newSound("sound/effects/enemy_explosion.ogg");
		coinSound = game.getAudio().newSound("sound/effects/coin.ogg");
		
		heroBulletSound = game.getAudio().newSound("sound/effects/hero_bullet.ogg");
		heroGettingHitSound = game.getAudio().newSound("sound/effects/hero_getting_hit.ogg");
		heroLevelUpSound = game.getAudio().newSound("sound/effects/level_up.ogg");
		heroItemUsedSound = game.getAudio().newSound("sound/effects/item_used.ogg");
		
		enemyFiringSound = game.getAudio().newSound("sound/effects/enemy_firing.ogg");
		enemyGettingHitDamagedSound = game.getAudio().newSound("sound/effects/enemy_getting_hit_damaged.ogg");
		enemyGettingHitNotDamagedSound = game.getAudio().newSound("sound/effects/enemy_getting_hit_not_damaged.ogg");
	}
	
	public static void playSoundHeroBullet(){
		if(System.currentTimeMillis() - timerHeroBullet >= timerHeroBulletCD){
			heroBulletSound.play(GameData.soundVolume/5);
			timerHeroBullet = System.currentTimeMillis();
		}
	}
	public static void playSoundHeroGettingHit(){
		if(System.currentTimeMillis() - timerHeroGettingHit >= timerSoundEffectCD){
			heroGettingHitSound.play(0.6f);
			timerHeroGettingHit = System.currentTimeMillis();
		}
	}
	public static void playSoundShipExplosion() { 
		if(System.currentTimeMillis() - timerShipExplosion >= timerSoundEffectCD){
			explosionSound.play(0.35f);
			timerShipExplosion = System.currentTimeMillis();
		}
	}
	public static void playSoundGameOver(){ gameOverSound.play(1f);}
	public static void playCoinSound() {
		if(System.currentTimeMillis() - timerCoin >= timerSoundEffectCD){
			coinSound.play(0.3f);
			timerCoin = System.currentTimeMillis();
		}
	}
	public static void playSoundLevelUp() { heroLevelUpSound.play(0.8f);	}
	public static void playSoundEnemyBullet() {
		if(System.currentTimeMillis() - timerEnemyBullet >= timerSoundEffectCD){
			enemyFiringSound.play(0.25f);
			timerEnemyBullet = System.currentTimeMillis();
		}
	}
	public static void playSoundEnemyGettingHitDamaged() {
		if(System.currentTimeMillis() - timerEnemyGettingHitDamaged >= timerSoundEffectCD){
			enemyGettingHitDamagedSound.play(0.2f);
			timerEnemyGettingHitDamaged = System.currentTimeMillis();
		}
	}
	public static void playSoundEnemyGettingHitNotDamaged() {
		if(System.currentTimeMillis() - timerEnemyGettingHitNotDamaged >= timerSoundEffectCD){
			enemyGettingHitNotDamagedSound.play(0.8f);
			timerEnemyGettingHitNotDamaged = System.currentTimeMillis();
		}
	}
	
	public static void playSoundItemUsed() {
		heroItemUsedSound.play(1f);
	}
}
