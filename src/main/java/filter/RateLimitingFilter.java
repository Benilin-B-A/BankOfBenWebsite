package filter;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class RateLimitingFilter implements Filter {

    private ConcurrentMap<String, Long> requestTimestamps = new ConcurrentHashMap<>();
    private int requestLimit = 10;
    private TimeUnit timeUnit = TimeUnit.SECONDS;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String clientIP = request.getRemoteAddr();
        long now = System.currentTimeMillis();
        long lastRequestTime = requestTimestamps.getOrDefault(clientIP, 0L);
        long elapsedTime = now - lastRequestTime;

        if (elapsedTime < timeUnit.toMillis(1) / requestLimit) {
            ((HttpServletResponse) response).setStatus(429);
            response.getWriter().write("Too many requests");
            return;
        }
        requestTimestamps.put(clientIP, now);
        chain.doFilter(request, response);
    }


	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}
}
