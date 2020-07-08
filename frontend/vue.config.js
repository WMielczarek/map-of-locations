module.exports = {
    devServer: {
        proxy: {
            '/api': {
                target: 'http://literary-map-server:8080/',
                changeOrigin: true
            },
            '/nlprest2': {
                target: 'http://ws.clarin-pl.eu',
                changeOrigin: true
            }
        }
    }
}