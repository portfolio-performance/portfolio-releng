package name.abuchen.portfolio.releng.poeditor;

import java.io.IOException;

public interface Task
{

    void perform(Config config, TermData data) throws IOException;

}
