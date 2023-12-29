package com.google.android.gms.games;

import android.accounts.Account;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.auth.api.signin.internal.SignInConfiguration;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.common.internal.IGmsCallbacks;
import com.google.android.gms.games.internal.IGamesCallbacks;
import com.google.android.gms.games.internal.IGamesService;
import com.google.android.gms.games.player.GamesPlayerManager;
import com.google.android.gms.games.player.PlayerColumns;

import org.microg.gms.BaseService;
import org.microg.gms.common.GmsService;
import org.microg.gms.games.signin.GamesSignInActivity;
import org.microg.gms.games.signin.GamesSignInManager;

import java.util.ArrayList;
import java.util.List;

public class GamesAndroidStubService extends BaseService {
    private static final String TAG = GamesAndroidStubService.class.getSimpleName();
    private Account account;
    private String srcPackageName;

    public GamesAndroidStubService() {
        super(TAG, GmsService.GAMES);
    }

    @Override
    public void handleServiceRequest(IGmsCallbacks callback, GetServiceRequest request, GmsService service) throws RemoteException {
        Log.i(TAG, String.format("GamesAndroidChimeraService.handleServiceRequest(request=%s, service=%s)", request, service));
        srcPackageName = request.packageName;
        account = GamesSignInManager.checkAccount(this, srcPackageName, request.account);
        callback.onPostInitComplete(CommonStatusCodes.SUCCESS, new GamesAndroidStubServiceImpl(this), Bundle.EMPTY);
    }

    class GamesAndroidStubServiceImpl extends IGamesService.Stub {
        private final Context context;

        GamesAndroidStubServiceImpl(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        public void clientDisconnecting(long clientId) throws RemoteException {
            Log.d(TAG, "Unimplemented method clientDisconnecting(clientId=" + clientId + ")");
        }

        @Override
        public void signOut(IGamesCallbacks callbacks) throws RemoteException {
            Log.d(TAG, "signOut(callbacks=" + callbacks + ")");
            callbacks.onSignOutComplete();
        }

        @Override
        public String getAppId() throws RemoteException {
            Log.d(TAG, "getAppId()");
            return "";
        }

        @Override
        public Bundle getConnectionHint() throws RemoteException {
            Log.d(TAG, "getConnectionHint()");
            return Bundle.EMPTY;
        }

        @Override
        public void showWelcomePopup(IBinder windowToken, Bundle extraArgs) throws RemoteException {
            if (extraArgs != null)
                extraArgs.size();
            Log.d(TAG, "showWelcomePopup(windowToken=" + windowToken + ", extraArgs=" + extraArgs + ")");
        }

        @Override
        public void cancelPopups() throws RemoteException {
            Log.d(TAG, "cancelPopups()");
        }

        @Override
        public String getCurrentAccountName() throws RemoteException {
            Log.d(TAG, "getCurrentAccountName()");
            return "";
        }

        @Override
        public void loadGameplayAclInternal(IGamesCallbacks callbacks, String gameId) throws RemoteException {
            Log.d(TAG, "loadGameplayAclInternal(callbacks=" + callbacks + ", gameId=" + gameId + ")");
        }

        @Override
        public void updateGameplayAclInternal(IGamesCallbacks callbacks, String gameId, String aclData) throws RemoteException {
            Log.d(TAG, "updateGameplayAclInternal(callbacks=" + callbacks + ", gameId=" + gameId + "aclData=" + aclData + ")");
        }

        @Override
        public void loadFAclInternal(IGamesCallbacks callbacks, String gameId) throws RemoteException {
            Log.d(TAG, "loadFAclInternal(callbacks=" + callbacks + ", gameId=" + gameId + ")");
        }

        @Override
        public void updateFAclInternal(IGamesCallbacks callbacks, String gameId, boolean allCirclesVisible, long[] circleIds) throws RemoteException {
            Log.d(TAG, "updateFAclInternal(callbacks=" + callbacks + ", gameId=" + gameId + ")");
        }

        @Override
        public String getCurrentPlayerId() throws RemoteException {
            Log.d(TAG, "getCurrentPlayerId()");
            return "111";
        }

        @Override
        public DataHolder getCurrentPlayer() throws RemoteException {
            Log.d(TAG, "getCurrentPlayer()");
            if (account != null) {
                DataHolder.Builder builder = DataHolder.builder(PlayerColumns.DEFAULT.getColumns(), null);
                builder.withRow(GamesPlayerManager.getInstance(context).getPlayer(account).toContentValues());
                return builder.build(0);
            } else {
                Log.w(TAG, "getCurrentPlayer: require sign in");
            }
            return null;
        }

        @Override
        public void loadPlayer(IGamesCallbacks callbacks, String playerId) throws RemoteException {
            Log.d(TAG, "loadPlayer(callbacks=+" + callbacks + ", playerId=" + playerId + ")");
        }

        @Override
        public void loadInvitablePlayers(IGamesCallbacks callbacks, int pageSize, boolean expandCachedData, boolean forceReload) throws RemoteException {
            Log.d(TAG, "loadInvitablePlayers(callbacks=+" + callbacks + ", pageSize=" + pageSize + ")");
        }

        @Override
        public void submitScore(IGamesCallbacks callbacks, String leaderboardId, long score) throws RemoteException {
            Log.d(TAG, "submitScore(callbacks=+" + callbacks + ", leaderboardId=" + leaderboardId + ", score=" + score + ")");
        }

        @Override
        public void loadLeaderboards(IGamesCallbacks callbacks) throws RemoteException {
            Log.d(TAG, "loadLeaderboards(callbacks=+" + callbacks + ")");
        }

        @Override
        public void loadLeaderboard(IGamesCallbacks callbacks, String leaderboardId) throws RemoteException {
            Log.d(TAG, "loadLeaderboard");
        }

        @Override
        public void loadTopScores(IGamesCallbacks callbacks, String leaderboardId, int span, int leaderboardCollection, int maxResults, boolean forceReload) throws RemoteException {
            Log.d(TAG, "loadTopScores");
        }

        @Override
        public void loadPlayerCenteredScores(IGamesCallbacks callbacks, String leaderboardId, int span, int leaderboardCollection, int maxResults, boolean forceReload) throws RemoteException {
            Log.d(TAG, "loadPlayerCenteredScores");
        }

        @Override
        public void loadMoreScores(IGamesCallbacks callbacks, Bundle previousheader, int maxResults, int pageDirection) throws RemoteException {
            Log.d(TAG, "loadMoreScores");
        }

        @Override
        public void loadAchievements(IGamesCallbacks callbacks) throws RemoteException {
            Log.d(TAG, "loadAchievements");
        }

        @Override
        public void revealAchievement(IGamesCallbacks callbacks, String achievementId, IBinder windowToken, Bundle extraArgs) throws RemoteException {
            Log.d(TAG, "revealAchievement");
        }

        @Override
        public void unlockAchievement(IGamesCallbacks callbacks, String achievementId, IBinder windowToken, Bundle extraArgs) throws RemoteException {
            Log.d(TAG, "unlockAchievement");
        }

        @Override
        public void incrementAchievement(IGamesCallbacks callbacks, String achievementId, int numSteps, IBinder windowToken, Bundle extraArgs) throws RemoteException {
            Log.d(TAG, "incrementAchievement");
        }

        @Override
        public void loadGame(IGamesCallbacks callbacks) throws RemoteException {
            Log.d(TAG, "loadGame");
        }

        @Override
        public void loadInvitations(IGamesCallbacks callbacks) throws RemoteException {
            Log.d(TAG, "loadInvitations");
        }

        @Override
        public void declineInvitation(String invitationId, int invitationType) throws RemoteException {
            Log.d(TAG, "declineInvitation");
        }

        @Override
        public void dismissInvitation(String invitationId, int invitationType) throws RemoteException {
            Log.d(TAG, "dismissInvitation");
        }

        @Override
        public void createRoom(IGamesCallbacks callbacks, IBinder processBinder, int variant, String[] invitedPlayerIds, Bundle autoMatchCriteria, boolean enableSockets, long clientId) throws RemoteException {
            Log.d(TAG, "createRoom");
        }

        @Override
        public void joinRoom(IGamesCallbacks callbacks, IBinder processBinder, String matchId, boolean enableSockets, long clientId) throws RemoteException {
            Log.d(TAG, "joinRoom");
        }

        @Override
        public void leaveRoom(IGamesCallbacks callbacks, String matchId) throws RemoteException {
            Log.d(TAG, "leaveRoom");
        }

        @Override
        public int sendReliableMessage(IGamesCallbacks callbacks, byte[] messageData, String matchId, String recipientParticipantId) throws RemoteException {
            Log.d(TAG, "sendReliableMessage");
            return 0;
        }

        @Override
        public int sendUnreliableMessage(byte[] messageData, String matchId, String[] recipientParticipantIds) throws RemoteException {
            Log.d(TAG, "sendUnreliableMessage");
            return 0;
        }

        @Override
        public String createSocketConnection(String participantId) throws RemoteException {
            Log.d(TAG, "createSocketConnection");
            return null;
        }

        @Override
        public void clearNotifications(int notificationTypes) throws RemoteException {
            Log.d(TAG, "clearNotifications");
        }

        @Override
        public void loadLeaderboardsFirstParty(IGamesCallbacks callbacks, String gameId) throws RemoteException {
            Log.d(TAG, "loadLeaderboardsFirstParty");
        }

        @Override
        public void loadLeaderboardFirstParty(IGamesCallbacks callbacks, String gameId, String leaderboardId) throws RemoteException {
            Log.d(TAG, "loadLeaderboardFirstParty");
        }

        @Override
        public void loadTopScoresFirstParty(IGamesCallbacks callbacks, String gameId, String leaderboardId, int span, int leaderboardCollection, int maxResults, boolean forceReload) throws RemoteException {
            Log.d(TAG, "loadTopScoresFirstParty");
        }

        @Override
        public void loadPlayerCenteredScoresFirstParty(IGamesCallbacks callbacks, String gameId, String leaderboardId, int span, int leaderboardCollection, int maxResults, boolean forceReload) throws RemoteException {
            Log.d(TAG, "loadPlayerCenteredScoresFirstParty");
        }

        @Override
        public void loadAchievementsFirstParty(IGamesCallbacks callbacks, String playerId, String gameId) throws RemoteException {
            Log.d(TAG, "loadAchievementsFirstParty");
        }

        @Override
        public void loadGameFirstParty(IGamesCallbacks callbacks, String gameId) throws RemoteException {
            Log.d(TAG, "loadGameFirstParty");
        }

        @Override
        public void loadGameInstancesFirstParty(IGamesCallbacks callbacks, String gameId) throws RemoteException {
            Log.d(TAG, "loadGameInstancesFirstParty");
        }

        @Override
        public void loadGameCollectionFirstParty(IGamesCallbacks callbacks, int pageSize, int collectionType, boolean expandCachedData, boolean forceReload) throws RemoteException {
            Log.d(TAG, "loadGameCollectionFirstParty");
        }

        @Override
        public void loadRecentlyPlayedGamesFirstParty(IGamesCallbacks callbacks, String externalPlayerId, int pageSize, boolean expandCachedData, boolean forceReload) throws RemoteException {
            Log.d(TAG, "loadRecentlyPlayedGamesFirstParty");
        }

        @Override
        public void loadInvitablePlayersFirstParty(IGamesCallbacks callbacks, int pageSize, boolean expandCachedData, boolean forceReload) throws RemoteException {
            Log.d(TAG, "loadInvitablePlayersFirstParty");
        }

        @Override
        public void loadRecentPlayersFirstParty(IGamesCallbacks callbacks) throws RemoteException {
            Log.d(TAG, "loadRecentPlayersFirstParty");
        }

        @Override
        public void loadCircledPlayersFirstParty(IGamesCallbacks callbacks, int pageSize, boolean expandCachedData, boolean forceReload) throws RemoteException {
            Log.d(TAG, "loadCircledPlayersFirstParty");
        }

        @Override
        public void loadSuggestedPlayersFirstParty(IGamesCallbacks callbacks) throws RemoteException {
            Log.d(TAG, "loadSuggestedPlayersFirstParty");
        }

        @Override
        public void dismissPlayerSuggestionFirstParty(String playerIdToDismiss) throws RemoteException {
            Log.d(TAG, "dismissPlayerSuggestionFirstParty");
        }

        @Override
        public void declineInvitationFirstParty(String gameId, String invitationId, int invitationType) throws RemoteException {
            Log.d(TAG, "declineInvitationFirstParty");
        }

        @Override
        public void loadInvitationsFirstParty(IGamesCallbacks callbacks, String gameId) throws RemoteException {
            Log.d(TAG, "loadInvitationsFirstParty");
        }

        @Override
        public int registerWaitingRoomListenerRestricted(IGamesCallbacks callbacks, String roomId) throws RemoteException {
            Log.d(TAG, "registerWaitingRoomListenerRestricted");
            return 0;
        }

        @Override
        public void setGameMuteStatusInternal(IGamesCallbacks callbacks, String gameId, boolean muted) throws RemoteException {
            Log.d(TAG, "setGameMuteStatusInternal");
        }

        @Override
        public void clearNotificationsFirstParty(String gameId, int notificationTypes) throws RemoteException {
            Log.d(TAG, "clearNotificationsFirstParty");
        }

        @Override
        public void loadNotifyAclInternal(IGamesCallbacks callbacks) throws RemoteException {
            Log.d(TAG, "loadNotifyAclInternal");
        }

        @Override
        public void updateNotifyAclInternal(IGamesCallbacks callbacks, String aclData) throws RemoteException {
            Log.d(TAG, "updateNotifyAclInternal");
        }

        @Override
        public void registerInvitationListener(IGamesCallbacks callbacks, long clientId) throws RemoteException {
            Log.d(TAG, "registerInvitationListener");
        }

        @Override
        public void unregisterInvitationListener(long clientId) throws RemoteException {
            Log.d(TAG, "unregisterInvitationListener");
        }

        @Override
        public int unregisterWaitingRoomListenerRestricted(String roomId) throws RemoteException {
            Log.d(TAG, "unregisterWaitingRoomListenerRestricted");
            return 0;
        }

        @Override
        public void isGameMutedInternal(IGamesCallbacks callbacks, String gameId) throws RemoteException {
            Log.d(TAG, "isGameMutedInternal");
        }

        @Override
        public void loadContactSettingsInternal(IGamesCallbacks callbacks) throws RemoteException {
            Log.d(TAG, "loadContactSettingsInternal");
        }

        @Override
        public void updateContactSettingsInternal(IGamesCallbacks callbacks, boolean enableMobileNotifications) throws RemoteException {
            Log.d(TAG, "updateContactSettingsInternal");
        }

        @Override
        public String getSelectedAccountForGameFirstParty(String gamePackageName) throws RemoteException {
            Log.d(TAG, "getSelectedAccountForGameFirstParty");
            return null;
        }

        @Override
        public void updateSelectedAccountForGameFirstParty(String gamePackageName, String accountName) throws RemoteException {
            Log.d(TAG, "updateSelectedAccountForGameFirstParty");
        }

        @Override
        public Uri getGamesContentUriRestricted(String gameId) throws RemoteException {
            Log.d(TAG, "getGamesContentUriRestricted");
            return null;
        }

        @Override
        public boolean shouldUseNewPlayerNotificationsFirstParty() throws RemoteException {
            Log.d(TAG, "shouldUseNewPlayerNotificationsFirstParty");
            return false;
        }

        @Override
        public void setUseNewPlayerNotificationsFirstParty(boolean newPlayerStyle) throws RemoteException {
            Log.d(TAG, "setUseNewPlayerNotificationsFirstParty");
        }

        @Override
        public void searchForPlayersFirstParty(IGamesCallbacks callbacks, String query, int pageSize, boolean expandCachedData, boolean forceReload) throws RemoteException {
            Log.d(TAG, "searchForPlayersFirstParty");
        }

        @Override
        public DataHolder getCurrentGame() throws RemoteException {
            Log.d(TAG, "getCurrentGame()");
            return null;
        }

        @Override
        public void reqServerSideAccess(IGamesCallbacks callbacks, String serverClientId, boolean forceRefreshToken) {
            Log.d(TAG, String.format("reqServerSideAccess(serverClientId=%s, forceRefreshToken=%b)", serverClientId, forceRefreshToken));
            List<Scope> scopes = new ArrayList();
            scopes.add(new Scope(Scopes.GAMES_LITE));
            GoogleSignInOptions options = new GoogleSignInOptions.Builder().requestScopes(scopes).setAccount(account)
                    .requestIdToken(false).requestServerAuthCode(true).forceCodeForRefreshToken(forceRefreshToken)
                    .serverClientId(serverClientId).build();
            SignInConfiguration signInConfiguration = new SignInConfiguration(srcPackageName, options);
            GamesSignInManager.silentSignIn(context, account, signInConfiguration,
                    new ResultReceiver(null) {
                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            SignInAccount signInAccount = resultData.getParcelable(GamesSignInManager.KEY_RESULT_DATA);
                            if (resultCode == 1 && signInAccount != null) {
                                try {
                                    callbacks.onServerAuthCodeUpdated(Status.SUCCESS, signInAccount.googleSignInAccount.getServerAuthCode());
                                } catch (RemoteException e) {
                                    Log.e(TAG, "reqServerSideAccess", e);
                                }
                            } else {
                                GamesSignInActivity.start(context, signInConfiguration, new ResultReceiver(null) {
                                    @Override
                                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                                        SignInAccount signInAccount = resultData.getParcelable(GamesSignInActivity.KEY_RESULT_DATA);
                                        if (resultCode == 1 && signInAccount != null) {
                                            try {
                                                callbacks.onServerAuthCodeUpdated(Status.SUCCESS, signInAccount.googleSignInAccount.getServerAuthCode());
                                            } catch (RemoteException e) {
                                                Log.e(TAG, "reqServerSideAccess1", e);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
            );
        }
    }
}
