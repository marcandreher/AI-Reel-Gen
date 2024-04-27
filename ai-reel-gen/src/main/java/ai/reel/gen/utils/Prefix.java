package ai.reel.gen.utils;

public enum Prefix {
	INFO(Color.CYAN + "[INFO]: " + Color.RESET), ERROR(Color.RED + "[ERROR]: " + Color.RESET),
	IMPORTANT(Color.RED + "[IMPORTANT]: " + Color.RED), WARNING(Color.YELLOW + "[WARNING]: " + Color.RESET), FFMPEG(Color.GREEN + "[FFMPEG]: " + Color.RESET),
	MYSQL(Color.BLUE + "[MYSQL]: " + Color.RESET), INPUT(Color.MAGENTA + "[INPUT]: " + Color.RESET), THEME(Color.GREEN + "[THEMING]: " + Color.RESET);

	private final String code;

	Prefix(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return code;
	}
}