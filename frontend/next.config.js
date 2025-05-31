/** @type {import('next').NextConfig} */

module.exports = {
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: `${process.env.BACKEND_URL}/:path*`
      },
    ];
  },
  async headers() {
    return [
      {
        source: '/api/:path*',
        headers: [
          { key: 'Access-Control-Allow-Credentials', value: 'true' },
          { key: 'Access-Control-Allow-Origin', value: `${process.env.DOMAIN_URL}` }
        ]
      }
    ]
  },
  webpackDevMiddleware: config => {
    config.watchOptions = {
      poll: 1000,
      aggregateTimeout: 300,
    }
    return config
  },
  env: {
    BACKEND_URL: process.env.BACKEND_URL,
    DOMAIN_URL: process.env.DOMAIN_URL
  },
};