package name.abuchen.portfolio.releng.poeditor;

public class Term
{
    private final String term;
    private final String context;

    public Term(String term, String context)
    {
        this.term = term;
        this.context = context;
    }

    public String getTerm()
    {
        return term;
    }

    public String getContext()
    {
        return context;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((context == null) ? 0 : context.hashCode());
        result = prime * result + ((term == null) ? 0 : term.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Term other = (Term) obj;
        if (context == null)
        {
            if (other.context != null)
                return false;
        }
        else if (!context.equals(other.context))
            return false;
        if (term == null)
        {
            if (other.term != null)
                return false;
        }
        else if (!term.equals(other.term))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Term [term=" + term + ", context=" + context + "]";
    }
}
