package com.jabyftw.gameclient.util.files.enums;

import com.jabyftw.gameclient.util.files.Resources;

/**
 * Created by Rafael on 08/12/2014.
 */
// English, Portuguese
public enum LangEnum {

    // Create connection state
    CONNECTING("CONNECTING...", "CONECTANDO..."),
    CONNECTING_WAITING_RESPONSE(CONNECTING.getDefaultValue(Resources.Language.ENGLISH), CONNECTING.getDefaultValue(Resources.Language.PORTUGUESE_BRAZIL)),
    CONNECTION_FAILED("FAILED TO CONNECT", "FALHA AO CONECTAR"),
    CONNECTED("CONNECTED!", "CONECTADO!"),
    PRESS_ENTER_TO_RECONNECT("Press ENTER to reconnect", "Pressione ENTER para reconectar"),
    PRESS_ESCAPE_TO_LEAVE("Press ESC to leave", "Pressione ESC para sair"),

    // Login/Register state
    USERNAME_INPUT_BUTTON("Username: %input%"),
    PASSWORD_INPUT_BUTTON("Password: %input%", "Senha: %input%"),

    SUCCESSFUL_LOGIN_RESPONSE("Successful login", "Login bem-sucedido"),
    SUCCESSFUL_REGISTER_RESPONSE("Successful registered", "Registro bem-sucedido"),

    WRONG_PASSWORD_RESPONSE("Wrong password!", "Senha incorreta"),
    INVALID_USERNAME_RESPONSE("Unkown username!", "Nome desconhecido"),

    USERNAME_ALREADY_TAKEN("Username already taken", "Nome ja usado"),
    USERNAME_TOO_SHORT("Username is too short", "Nome muito curto"),
    USERNAME_TOO_LONG("Username is too long", "Nome muito longo"),

    PASSWORD_TOO_SHORT("Password is too short", "Senha muito curta"),
    PASSWORD_TOO_LONG("Password is too long", "Senha muito longa"),

    UNKNOWN_ERROR("Unknown error", "Erro desconhecido"),

    // Create profile state
    CREATE_PROFILE_TITLE("CREATE PROFILE", "CRIAR PERFIL"),
    PLAYER_NAME("Player name: %input%", "Nome do jogador: %input%"),

    // Start Menu
    GAME_TITLE(true, "<game name here>"),
    PLAY_BUTTON("Play", "Jogar"),
    SETTINGS_BUTTON("Settings", "Configuracoes"),
    MAP_EDITOR_BUTTON("Map Editor", "Editor de Mapas"),
    EXIT_BUTTON("Exit", "Sair"),

    // PrePlayState menu
    SELECT_MAP_TITLE("Select your map", "Selecione seu mapa"),
    SELECT_MAP_BUTTON("Map: %mapname%", "Mapa: %mapname%"),
    NO_MAPS_AVAILABLE("No maps available", "Nao ha mapas disponiveis"),

    // PlayState Menu
    PAUSE_MENU_TITLE("Menu", "Menu"),
    CHANGE_LAYOUT("Change layout", "Mudar de classe"),
    EDIT_MAP("Edit current map", "Editar este mapa"),
    EXPORT_MAP("Export map", "Exportar este mapa"),
    TARGET_RECEIVED_DAMAGE("%damagetaken% damage", "%damagetaken% dano"),
    DO_INTERACTION("Press ENTER", "Aperte ENTER"),

    // Change layout menu
    CHANGE_LAYOUT_TITLE("CHANGE LAYOUT", "MUDAR CLASSE"),
    SELECT_LAYOUT_BUTTON("Layout: %layoutname%", "Classe: %layoutname%"),

    // Weapon language
    RELOADING_HINT("[RELOAD]", "[RECARREGAR]"),
    RELOADING_INFO("[RELOADING - %percentage%%]", "[RECARREGANDO - %percentage%%]"),
    CURRENT_WEAPON_CAPACITY("%currentweaponcapacity%"),
    MAXIMUM_WEAPON_CAPACITY("/%maximumweaponcapacity%"),

    // Config Menu
    BACK_BUTTON("Back", "Voltar"),
    BACK_TO_TITLE_BUTTON("Back to start menu", "Voltar ao menu inicial"),
    SELECTED_LANGUAGE_BUTTON(true, "Language: %selected%"),
    SHADOW_OFFSET_BUTTON("Shadow offset: %shadowoffset%", "Distancia da sombra: %shadowoffset%"),
    ENTER_FULLSCREEN("Go fullscreen", "Entrar em tela cheia"),
    LEAVE_FULLSCREEN("Leave fullscreen", "Sair de tela cheia"),
    RESTORE_DEFAULT_SCREEN_SIZE("Restore default screen size", "Restaurar tamanho padrao da tela"),
    ENTER_BUTTON("Accept", "Aceitar"),
    FAILED_ENTER_BUTTON(" [%fail%]"),

    // Map Editor
    MAP_EDITOR_PREFERENCES("Map preferences", "Preferencias do mapa"),
    MAP_EDITOR_OPTIONS("Map editor options", "Opcoes do editor de mapa"),
    WIDTH_FOR_MAP("Map's width: %width% (%ratio%x screens)", "Largura do mapa: %width% (%ratio%x telas)"),
    HEIGHT_FOR_MAP("Map's height: %height% (%ratio%x screens)", "Altura do mapa: %height% (%ratio%x telas)"),
    DISPLAY_NAME_FOR_MAP("Map name: %input%", "Nome do mapa: %input%"),
    MAP_NAME_TOO_LONG("Map name too long", "Nome do mapa muito longo"),
    MAP_NAME_TOO_SHORT("Map name too short", "Nome do mapa muito curto"),
    TEST_MAP("Test map", "Testar mapa");

    // Play State

    public static final int LANG_VERSION = -995;

    private final boolean alwaysUpdatedOnFile;
    private final String[] values = new String[Resources.Language.values().length];

    private LangEnum(boolean alwaysUpdatedOnFile, String... values) {
        if(values.length > this.values.length)
            throw new ArrayIndexOutOfBoundsException("Can't load strings for lang " + this.name());

        this.alwaysUpdatedOnFile = alwaysUpdatedOnFile;
        for(int i = 0; i < this.values.length; i++) {
            int valueIndex = i;

            while(valueIndex >= values.length) {
                valueIndex--;
            }

            this.values[i] = values[valueIndex];
        }
    }

    private LangEnum(String... values) {
        this(false, values);
    }

    public String getDefaultValue(Resources.Language language) {
        return values[language.ordinal()];
    }

    public boolean isAlwaysUpdatedOnFile() {
        return alwaysUpdatedOnFile;
    }
}
